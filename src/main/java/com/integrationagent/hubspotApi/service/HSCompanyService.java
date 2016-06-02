package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.domain.HSCompany;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HSCompanyService {

    private HttpService httpService;

    public HSCompanyService(HttpService httpService) {
        this.httpService = httpService;
    }

	public HSCompany create(HSCompany HSCompany) throws HubSpotException {
		String url = "/companies/v2/companies/";
		JsonNode jsonBody = httpService.postRequest(url, HSCompany.toJsonString(), null);
		HSCompany.setId(jsonBody.getObject().getLong("companyId"));
		return HSCompany;

    }

	public void addContact(String contactId, String companyId) throws HubSpotException {
		String url = "/companies/v2/companies/" + companyId + "/contacts/" + contactId;
		JsonNode jsonNode = httpService.putRequest(url, "");
	}

	private HSCompany parseCompanyData(JSONObject jsonBody) {
		HSCompany company = new HSCompany();

		company.setId(jsonBody.getLong("companyId"));

		JSONObject jsonProperties = jsonBody.getJSONObject("properties");

		Set<String> keys = jsonProperties.keySet();

		keys.stream().forEach(key ->
				company.setProperty(key,
						jsonProperties.get(key) instanceof JSONObject ?
								((JSONObject) jsonProperties.get(key)).getString("value") :
								jsonProperties.get(key).toString()
				)
		);

		return company;
	}


	public List<HSCompany> getByDomain(String domain) throws HubSpotException {

		List<HSCompany> companies = new ArrayList<>();

		String url = "/companies/v2/companies/domain/" + domain;

		JsonNode jsonNode = httpService.getRequest(url);

		if(jsonNode.isArray()){
			for (int i = 0; i < jsonNode.getArray().length(); i++) {
				companies.add(parseCompanyData(jsonNode.getArray().optJSONObject(i)));
			}
		}

		return companies;
	}

	public HSCompany update(HSCompany company) throws HubSpotException {

		String url = "/companies/v2/companies/" + company.getId();

		String properties = company.toJsonString();

		try {
			JsonNode jsonBody = httpService.putRequest(url, properties);
			return company;
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update company: " + company + ". Reason: " + e.getMessage(), e);
		}
	}
}
