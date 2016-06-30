package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.domain.HSEngagement;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class HSEngagementService {

    HttpService httpService;

    public HSEngagementService(HttpService httpService) {
        this.httpService = httpService;
    }

     public Long create(Long contactId, String body) throws HubSpotException {
        HSEngagement engagement = new HSEngagement(HSEngagement.Type.NOTE, contactId, body);
        String url = "/engagements/v1/engagements";

        JSONObject jsonNode = (JSONObject) httpService.postRequest(url, engagement.toString());

        return jsonNode.getJSONObject("engagement").getLong("id");

    }

    public void delete(Long engagementId) throws UnirestException, HubSpotException {
        String url = "/engagements/v1/engagements/" + engagementId;
        httpService.deleteRequest(url);
    }
}
