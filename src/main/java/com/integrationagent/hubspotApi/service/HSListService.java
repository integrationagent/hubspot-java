package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;

public class HSListService {

    private HttpService httpService;

    public HSListService(HttpService httpService) {
        this.httpService = httpService;
    }

    public Long getByID(String listId) throws HubSpotException {
        String url = "/contacts/v1/lists/" + listId;
        try {
            JsonNode jsonNode = httpService.getRequest(url);
            return jsonNode.getObject().getLong("listId");
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public Long create(String name, String portalId) throws HubSpotException {
        String url = "/contacts/v1/lists";
        String properties = new JSONObject()
                .put("name", name)
                .put("dynamic", false)
                .put("portalId", portalId)
                .toString();

        JsonNode jsonNode = httpService.postRequest(url, properties);
        return jsonNode.getObject().getLong("listId");
    }

    public void delete(String listId) throws HubSpotException {
        String url = "/contacts/v1/lists/" + listId;
        JsonNode jsonNode = httpService.deleteRequest(url);
    }

    public void assign(Long listId, Long contactId) throws HubSpotException {
        String url = "/contacts/v1/lists/" + listId + "/add";
        String properties = new JSONObject().put("vids", new JSONArray().put(contactId)).toString();
        httpService.postRequest(url, properties);
    }
}
