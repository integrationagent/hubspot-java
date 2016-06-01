package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.domain.HSCompany;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class HSCompanyService {

    private HttpService httpService;

    public HSCompanyService(HttpService httpService) {
        this.httpService = httpService;
    }

    public HSCompany createCompany(HSCompany HSCompany) throws HubSpotException {
        String url = "/companies/v2/companies/";
        JsonNode jsonBody = httpService.postRequest(url, HSCompany.toJsonString());
        HSCompany.setId(jsonBody.getObject().getLong("companyId"));
        return HSCompany;

    }

    public void addContactToCompany(String contactId, String companyId) throws HubSpotException {
        String url = "/companies/v2/companies/" + companyId + "/contacts/" + contactId;
        JsonNode jsonNode = httpService.putRequest(url, "");
    }
}
