package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.utils.HubSpotException;
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
            JSONObject jsonObject = (JSONObject) httpService.getRequest(url);

            if(!jsonObject.has("listId")){
                return null;
            }

            return jsonObject.getLong("listId");
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

        JSONObject jsonObject = (JSONObject) httpService.postRequest(url, properties);
        return jsonObject.getLong("listId");
    }

    public void delete(String listId) throws HubSpotException {
        String url = "/contacts/v1/lists/" + listId;
        httpService.deleteRequest(url);
    }

    public void assign(Long listId, Long contactId) throws HubSpotException {
        String url = "/contacts/v1/lists/" + listId + "/add";
        String properties = new JSONObject().put("vids", new JSONArray().put(contactId)).toString();
        httpService.postRequest(url, properties);
    }
}
