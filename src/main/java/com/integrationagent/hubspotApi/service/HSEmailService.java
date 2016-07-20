package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.utils.HubSpotException;

public class HSEmailService {

    private HttpService httpService;

    public HSEmailService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsubscribeFromAll(String email) throws HubSpotException {
        httpService.putRequest("/email/public/v1/subscriptions/" + email, "{\"unsubscribeFromAll\" :true}");
    }
}
