package com.integrationagent.hubspotApi.service;

import com.google.common.base.Strings;
import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class HSContactService {

    private HttpService httpService;

    public HSContactService(HttpService httpService) {
        this.httpService = httpService;
    }

    public HSContact getByEmail(String email) throws HubSpotException {
        String url = "/contacts/v1/contact/email/" + email + "/profile";
        return getContact(url);
    }

    public HSContact getByID(long id) throws HubSpotException{
        String url = "/contacts/v1/contact/vid/" + id + "/profile";
        return getContact(url);
    }

    private HSContact getContact(String url) throws HubSpotException {
        try {
            return parseContactData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public HSContact create(HSContact HSContact) throws HubSpotException {
        if (Strings.isNullOrEmpty(HSContact.getEmail())) {
            throw new HubSpotException("User email must be provided");
        }

        String url = "/contacts/v1/contact";

        JSONObject jsonObject = (JSONObject) httpService.postRequest(url, HSContact.toJsonString());
        HSContact.setId(jsonObject.getLong("vid"));
        return HSContact;
    }

    public HSContact update(HSContact contact) throws HubSpotException {
        if (contact.getId() == 0) {
            throw new HubSpotException("User ID must be provided");
        }

        String url = "/contacts/v1/contact/vid/" + contact.getId() + "/profile";
        JSONObject jsonObject = (JSONObject) httpService.postRequest(url, contact.toJsonString());
        return contact;
    }

    public HSContact updateOrCreate(HSContact HSContact) throws HubSpotException {
        if (Strings.isNullOrEmpty(HSContact.getEmail())) {
            throw new HubSpotException("User email must be provided");
        }

        String url = "/contacts/v1/contact/createOrUpdate/email/" + HSContact.getEmail();
        JSONObject jsonObject = (JSONObject) httpService.postRequest(url, HSContact.toJsonString());
        HSContact.setId(jsonObject.getLong("vid"));
        return HSContact;
    }

    public void updateOrCreateContacts(List<HSContact> HSContacts) throws HubSpotException {
        String url = "/contacts/v1/contact/batch/";
        JSONArray array = new JSONArray();

        for (HSContact HSContact : HSContacts) {
            JSONObject jsonObject = HSContact.toJson();
            jsonObject.put("email", HSContact.getEmail());
            array.put(jsonObject);
        }

        httpService.postRequest(url, array.toString());
    }

    public void delete(HSContact contact) throws HubSpotException {
        if (contact.getId() == 0) {
            throw new HubSpotException("User ID must be provided");
        }
        String url = "/contacts/v1/contact/vid/" + contact.getId();

        httpService.deleteRequest(url);
    }

    public HSContact parseContactData(JSONObject jsonObject) {
        HSContact HSContact = new HSContact();
        HSContact.setId(jsonObject.getLong("vid"));
        JSONObject jsonProperties = jsonObject.getJSONObject("properties");
        Set<String> keys = jsonProperties.keySet();
        keys.stream().forEach( key ->
                HSContact.setProperty(key,
                        jsonProperties.get(key) instanceof JSONObject ?
                                ((JSONObject) jsonProperties.get(key)).getString("value") :
                                jsonProperties.get(key).toString()
                )
        );
        return HSContact;
    }
}
