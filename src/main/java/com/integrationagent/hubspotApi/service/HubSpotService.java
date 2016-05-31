package com.integrationagent.hubspotApi.service;

import com.google.common.base.Strings;
import com.integrationagent.hubspotApi.domain.HSCompany;
import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.domain.HSEngagement;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Author: dlunev
 * Date: 2/18/15 9:15 AM
 */


public class HubSpotService {

	private String API_KEY;
	private String PORTAL_ID;
	private static final String API_HOST = "https://api.hubapi.com";

	public HubSpotService(String api_key) {
		this.API_KEY = api_key;
	}

	public HubSpotService(String api_key, String portalId) {
		this.API_KEY = api_key;
		this.PORTAL_ID = portalId;
	}

	public HSCompany createCompany(HSCompany HSCompany) throws HubSpotException {

		String url = API_HOST + "/companies/v2/companies/";
		String properties = HSCompany.toJsonString();

		try {
			JsonNode jsonBody = postRequest(url, properties);
			HSCompany.setId(jsonBody.getObject().getLong("companyId"));
			return HSCompany;
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot create HSCompany: " + HSCompany + ". Reason: " + e.getMessage(), e);
		}
	}

	public void addContactToCompany(String contactId, String companyId) throws HubSpotException {

		String url = API_HOST + "/companies/v2/companies/" + companyId + "/contacts/" + contactId;

		try {
			HttpResponse<JsonNode> resp = Unirest
					.put(url)
					.queryString("hapikey", API_KEY)
					.header("accept", "application/json")
					.header("Content-Type", "application/json")
					.body("")
					.asJson();

		} catch (UnirestException e) {
			throw new HubSpotException("", e);
		}
	}

	public void updateOrCreateContacts(List<HSContact> HSContacts) throws HubSpotException {

		String url = API_HOST + "/HSContacts/v1/contact/batch/";

		JSONArray array = new JSONArray();

		for (HSContact HSContact : HSContacts) {
			JSONObject jsonObject = HSContact.toJson();
			jsonObject.put("email", HSContact.getEmail());
			array.put(jsonObject);
		}

		postRequest(url, array.toString());
	}



	public Long getList(String listId) throws HubSpotException {
		String url = "/contacts/v1/lists/" + listId;

		try {
			JsonNode jsonNode = getRequest(url);
			//do we need list class ?!
			return jsonNode.getObject().getLong("listId");
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot get list: " + listId + ". Reason: " + e.getMessage(), e);
		}
	}

	public Long createList(String name, String portalId) throws HubSpotException {
		String url = API_HOST + "/contacts/v1/lists";

		String properties = new JSONObject()
				.put("name", name)
				.put("dynamic", false)
				.put("portalId", portalId)
				.toString();

		try {
			JsonNode jsonNode = postRequest(url, properties);
			return jsonNode.getObject().getLong("listId");
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot create list: " + name + ". Reason: " + e.getMessage(), e);
		}
	}

	public void deleteList(String listId) throws HubSpotException {
		String url = API_HOST + "/contacts/v1/lists/" + listId;

		try {
			JsonNode jsonNode = deleteRequest(url);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot delete list: " + listId + ". Reason: " + e.getMessage(), e);
		}
	}

	public void unsubscribeFromAll(String email) throws HubSpotException{

		putRequest("/email/public/v1/subscriptions/" + email, "{\"unsubscribeFromAll\" :true}");
	}

	public JsonNode putRequest(String url, String properties) throws HubSpotException {
		try {
			HttpResponse<JsonNode> resp = Unirest
                    .put(API_HOST + url)
                    .header("accept", "application/json")
					.header("Content-Type", "application/json")
					.queryString("hapikey", API_KEY)
                    .body(properties)
					.asJson();

			if(204 != resp.getStatus() && 202 != resp.getStatus() && 200 != resp.getStatus()){
				throw new HubSpotException(new JSONObject(resp.getBody().toString()).toString(2));
			}

			return resp.getBody();
		} catch (UnirestException e) {
			throw new HubSpotException("Can not get data", e);
		}
	}

	public JsonNode getRequest(String url) throws HubSpotException {
		try {
			HttpResponse<JsonNode> resp = Unirest
                    .get(API_HOST + url)
                    .queryString("hapikey", API_KEY)
                        .asJson();

			checkResponse(resp);

			return resp.getBody();
		} catch (UnirestException e) {
			throw new HubSpotException("Can not get data", e);
		}
	}

	public JsonNode postRequest(String url, String properties) throws HubSpotException {
		try {
			HttpResponse<JsonNode> resp = Unirest
					.post(url)
					.queryString("hapikey", API_KEY)
					.header("accept", "application/json")
					.header("Content-Type", "application/json")
                    .body(properties)
					.asJson();

			checkResponse(resp);

			return resp.getBody();
		} catch (UnirestException e) {
			throw new HubSpotException("Cannot make a request: \n" + properties, e);
		}
	}

	public JsonNode deleteRequest(String url) throws HubSpotException {
		try {
			HttpResponse<JsonNode> resp = Unirest
					.delete(url)
					.queryString("hapikey", API_KEY)
					.asJson();

			checkResponse(resp);

			return resp.getBody();
		} catch (UnirestException e) {
			throw new HubSpotException("Cannot make delete request", e);
		}

	}

	public void assignList(Long listId, Long contactId) throws HubSpotException {
		String url = API_HOST + "/contacts/v1/lists/" + listId + "/add";

		String properties = new JSONObject()
					.put("vids", new JSONArray()
					.put(contactId))
					.toString();

		try {
			postRequest(url, properties);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot assign list " + listId + ". Reason: " + e.getMessage(), e);
		}
	}

	public Long logEngagement(HSEngagement HSEngagement) throws HubSpotException {

		String url = API_HOST + "/engagements/v1/engagements";
		JsonNode jsonNode = postRequest(url, HSEngagement.toString());

		return jsonNode.getObject()
				.getJSONObject("HSEngagement")
				.getLong("id");

	}

	public void deleteEngagement(Long engagementId) throws UnirestException {

		String url = API_HOST + "/engagements/v1/engagements/" + engagementId;
		Unirest
					.delete(url)
					.queryString("hapikey", API_KEY)
					.header("accept", "application/json")
					.header("Content-Type", "application/json")
                    .asJson();
	}

	public void logEngagement(Long contactId, String body) throws HubSpotException {

		try {
			logEngagement(new HSEngagement(HSEngagement.Type.NOTE, contactId, body));
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot log NOTE for contact " + contactId + ". Reason: " + e.getMessage(), e);
		}
	}

	private void checkResponse(HttpResponse<JsonNode> resp) throws HubSpotException{
		if(204 != resp.getStatus() && 202 != resp.getStatus() && 200 != resp.getStatus()){
			String message = resp.getBody().getObject().getString("message");
			if (!Strings.isNullOrEmpty(message)) {
				throw new HubSpotException(new JSONObject(resp.getBody().toString()).toString(2));
			} else {
				throw new HubSpotException(new JSONObject(resp.getBody().toString()).toString(2), resp.getStatus());
			}
		}
	}


}
