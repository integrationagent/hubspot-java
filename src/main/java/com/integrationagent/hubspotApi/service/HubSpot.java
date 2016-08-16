package com.integrationagent.hubspotApi.service;

public class HubSpot {

    private String apiBase = "https://api.hubapi.com";
    private HttpService httpService;

    public HubSpot(String apiKey) {
        httpService = new HttpService(apiKey, apiBase);
    }

    public HSCompanyService company() {
        return new HSCompanyService(httpService);
    }

    public HSContactService contact() {
        return new HSContactService(httpService);
    }

    public HSDealService deal() {
        return new HSDealService(httpService);
    }

    public HSEmailService email() {
        return new HSEmailService(httpService);
    }

    public HSEngagementService engagement() {
        return new HSEngagementService(httpService);
    }

    public HSFormService form() {
        return new HSFormService(httpService);
    }

    public HSListService list() {
        return new HSListService(httpService);
    }

    public HSWorkflowService workflow() {
        return new HSWorkflowService(httpService);
    }
}
