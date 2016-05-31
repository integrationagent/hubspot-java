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

    public JsonNode getRequest(String url) throws HubSpotException {
        try {
            System.out.println(apiKey);
            HttpResponse<JsonNode> resp = Unirest
                    .get(apiBase + url)
                    .queryString("hapikey", apiKey)
                    .asJson();

            checkResponse(resp);

            return resp.getBody();
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data", e);
        }
    }

    public JsonNode postRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = Unirest
                    .post(apiBase + url)
                    .queryString("hapikey", apiKey)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(properties)
                    .asJson();

            checkResponse(resp);

            return resp.getBody();
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make a request: \n" + properties, e);
        }
    }

    public JsonNode deleteRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = Unirest
                    .delete(apiBase + url)
                    .queryString("hapikey", apiKey)
                    .asJson();

            checkResponse(resp);

            return resp.getBody();
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make delete request", e);
        }

    }

    private void checkResponse(HttpResponse<JsonNode> resp) throws HubSpotException {
        if(204 != resp.getStatus() && 200 != resp.getStatus()){
            String message = resp.getBody().getObject().getString("message");
            if (!Strings.isNullOrEmpty(message)) {
                throw new HubSpotException(message, resp.getStatus());
            } else {
                throw new HubSpotException(resp.getStatusText(), resp.getStatus());
            }
        }
    }
}
