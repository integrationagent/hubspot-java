package com.integrationagent.hubspotApi.service;

import com.google.common.base.Strings;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class HttpService {
    
    private String apiKey;
    private String apiBase;

    public HttpService(String apiKey, String apiBase) {
        this.apiKey = apiKey;
        this.apiBase = apiBase;
    }

    public Object getRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = Unirest
                    .get(apiBase + url)
                    .queryString("hapikey", apiKey)
                    .asJson();

            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data\n URL:" + url, e);
        }
    }

    public Object postRequest(String url, String properties) throws HubSpotException {
        return postRequest(url, properties, "application/json");
    }

    public Object postRequest(String url, String properties, String contentType) throws HubSpotException {
        if (Strings.isNullOrEmpty(contentType)) {
            contentType = "application/json";
        }
        try {
            HttpResponse<JsonNode> resp = Unirest
                    .post(apiBase + url)
                    .queryString("hapikey", apiKey)
                    .header("accept", "application/json")
                    .header("Content-Type", contentType)
                    .body(properties)
                    .asJson();

            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make a request: \n" + properties, e);
        }
    }

    public Object deleteRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = Unirest
                    .delete(apiBase + url)
                    .queryString("hapikey", apiKey)
                    .asJson();

            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make delete request: \n URL: " + url, e);
        }
    }

    public Object putRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = Unirest
                    .put(apiBase + url)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .queryString("hapikey", apiKey)
                    .body(properties)
                    .asJson();

            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data", e);
        }
    }

    private Object checkResponse(HttpResponse<JsonNode> resp) throws HubSpotException {
        if(204 != resp.getStatus() && 200 != resp.getStatus() && 202 != resp.getStatus()){
            String message = null;
            try {
                message = (resp.getStatus() == 404) ? resp.getStatusText() : resp.getBody().getObject().getString("message");
            } catch (Exception e) {
            }

            if (!Strings.isNullOrEmpty(message)) {
                throw new HubSpotException(message, resp.getStatus());
            } else {
                throw new HubSpotException(resp.getStatusText(), resp.getStatus());
            }
        } else {
            if (resp.getBody() != null) {
                return resp.getBody().isArray() ? resp.getBody().getArray() : resp.getBody().getObject();
            } else {
                return null;
            }
        }
    }
}
