package com.integrationagent.hubspotApi.service;

public class HSGateway {

    private String apiBase = "https://api.hubapi.com";
    private HttpService httpService;

    public HSGateway(String apiKey) {
        httpService = new HttpService(apiKey, apiBase);
    }

    public HSContactGateway contact() {
        return new HSContactGateway(httpService);
    }
}
