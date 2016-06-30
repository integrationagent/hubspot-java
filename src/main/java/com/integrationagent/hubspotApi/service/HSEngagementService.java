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
<<<<<<< HEAD
        JsonNode jsonNode = httpService.postRequest(url, engagement.toString(), null);

        return jsonNode.getObject().getJSONObject("engagement").getLong("id");
=======
        JSONObject jsonObject = (JSONObject) httpService.postRequest(url, engagement.toString());
        return jsonObject.getJSONObject("HSEngagement").getLong("id");
>>>>>>> 4a410bcf30cb24e662abb7c37df903b4b44ac7f6
    }

    public void delete(Long engagementId) throws UnirestException, HubSpotException {
        String url = "/engagements/v1/engagements/" + engagementId;
        httpService.deleteRequest(url);
    }
}
