package com.integrationagent.hubspotApi.service;

import com.google.common.base.Strings;
import com.integrationagent.hubspotApi.HubSpot;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class HttpService {

    public static JsonNode getRequest(String url) throws HubSpotException {
        try {
            System.out.println(HubSpot.apiKey);
            HttpResponse<JsonNode> resp = Unirest
                    .get(HubSpot.getApiBase() + url)
                    .queryString("hapikey", HubSpot.apiKey)
                    .asJson();

            checkResponse(resp);

            return resp.getBody();
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data", e);
        }
    }

    public static JsonNode postRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = Unirest
                    .post(HubSpot.getApiBase() + url)
                    .queryString("hapikey", HubSpot.apiKey)
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

    public static JsonNode deleteRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = Unirest
                    .delete(url)
                    .queryString("hapikey", HubSpot.apiKey)
                    .asJson();

            checkResponse(resp);

            return resp.getBody();
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make delete request", e);
        }

    }

    private static void checkResponse(HttpResponse<JsonNode> resp) throws HubSpotException{
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
