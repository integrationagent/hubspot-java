package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.domain.HSForm;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.integrationagent.hubspotApi.utils.UrlMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class HSFormService {

    private HttpService httpService;

    public HSFormService(HttpService httpService) {
        this.httpService = httpService;
    }

    public List<HSForm> getAll() throws HubSpotException {

        String url = "/forms/v2/forms";

        List<HSForm> forms = new ArrayList<>();

        JSONArray jsonArray = (JSONArray)httpService.getRequest(url);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            HSForm hsForm = new HSForm();

            hsForm.setGuid(jsonObject.getString("guid"));
            hsForm.setName(jsonObject.getString("name"));

            JSONArray formFieldGroups = jsonObject.getJSONArray("formFieldGroups");

            for (int j = 0; j < formFieldGroups.length(); j++) {
                JSONObject formField = formFieldGroups.getJSONObject(j);
                HSForm.Field field = hsForm.new Field();

                field.setName(formField.getJSONArray("fields").getJSONObject(0).getString("name"));
                field.setDefaultValue(formField.getJSONArray("fields").getJSONObject(0).getString("defaultValue"));

                hsForm.addField(field);
            }

            forms.add(hsForm);
        }

        return forms;
    }

    public void submit(String formId, UrlMap properties, String PortalId) throws HubSpotException {
        String url = "https://forms.hubspot.com/uploads/form/v2/" + PortalId + "/" + formId;
        httpService.postRequest(url, properties.toString(), "application/x-www-form-urlencoded");
    }
}
