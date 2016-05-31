package com.integrationagent.hubspotApi.service;

import com.google.common.base.Strings;
import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONObject;

import java.util.Set;

public class HSContactGateway {

    private HttpService httpService;

    public HSContactGateway(HttpService httpService) {
        this.httpService = httpService;
    }

    public HSContact findByEmail(String email) throws HubSpotException {
        String url = "/contacts/v1/contact/email/" + email + "/profile";
        return parseContactData(httpService.getRequest(url));
    }

    public HSContact findById(long id) throws HubSpotException{
        String url = "/contacts/v1/contact/vid/" + id + "/profile";
        return parseContactData(httpService.getRequest(url));
    }

    public HSContact create(HSContact HSContact) throws HubSpotException {
        if (Strings.isNullOrEmpty(HSContact.getEmail())) {
            throw new HubSpotException("User email must be provided");
        }

        String url = "/contacts/v1/HSContact";

        JsonNode jsonBody = httpService.postRequest(url, HSContact.toJsonString());
        HSContact.setId(jsonBody.getObject().getLong("vid"));
        return HSContact;

    }

    public HSContact update(HSContact contact) throws HubSpotException {
        if (contact.getId() == 0) {
            throw new HubSpotException("User ID must be provided");
        }

        String url = "/contacts/v1/HSContact/vid/" + contact.getId() + "/profile";
        JsonNode jsonBody = httpService.postRequest(url, contact.toJsonString());
        return contact;
    }

    public HSContact updateOrCreate(HSContact HSContact) throws HubSpotException {
        if (Strings.isNullOrEmpty(HSContact.getEmail())) {
            throw new HubSpotException("User email must be provided");
        }

        String url = "/contacts/v1/HSContact/createOrUpdate/email/" + HSContact.getEmail();
        JsonNode jsonBody = httpService.postRequest(url, HSContact.toJsonString());
        HSContact.setId(jsonBody.getObject().getLong("vid"));
        return HSContact;
    }

    public void delete(HSContact contact) throws HubSpotException {
        if (contact.getId() == 0) {
            throw new HubSpotException("User ID must be provided");
        }
        String url = "/contacts/v1/HSContact/vid/" + contact.getId();

        try {
            JsonNode jsonBody = httpService.deleteRequest(url);
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot update HSContact: " + contact.getId() + ". Reason: " + e.getMessage(), e);
        }
    }

    private HSContact parseContactData(JsonNode jsonBody) {
        HSContact HSContact = new HSContact();

        HSContact.setId(jsonBody.getObject().getLong("vid"));

        JSONObject jsonProperties = jsonBody.getObject().getJSONObject("properties");

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
