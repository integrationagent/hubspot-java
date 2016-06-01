package com.integrationagent.hubspotApi.service;

public class HubSpot {

    private String apiBase = "https://api.hubapi.com";
    private HttpService httpService;

    public HubSpot(String apiKey) {
        httpService = new HttpService(apiKey, apiBase);
    }

    public HSContactService contact() {
        return new HSContactService(httpService);
    }

    public HSCompanyService company() {
        return new HSCompanyService(httpService);
    }

    public HSEngagementService engagement() {
        return new HSEngagementService(httpService);
    }

    public HSFormsService form() {
        return new HSFormsService(httpService);
    }

    public HSListService list() {
        return new HSListService(httpService);
    }

    public HSSubscriptionsService subscription() {
        return new HSSubscriptionsService(httpService);
    }

    public HSWorkflowService workflow() {
        return new HSWorkflowService(httpService);
    }
}
