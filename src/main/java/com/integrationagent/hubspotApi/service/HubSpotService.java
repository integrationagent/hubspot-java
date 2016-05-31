package com.integrationagent.hubspotApi.service;

import com.google.common.base.Strings;
import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Author: dlunev
 * Date: 2/18/15 9:15 AM
 */


public class HubSpotService {

	/*private HttpService httpService;

	public HubSpotService() throws IOException {
		httpService = new HttpService();
	}

	public Long getList(String listId) throws HubSpotException {
		String url = "/contacts/v1/lists/" + listId;

		try {
			JsonNode jsonNode = httpService.getRequest(url);
			//do we need list class ?!
			return jsonNode.getObject().getLong("listId");
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot get list: " + listId + ". Reason: " + e.getMessage(), e);
		}
	}

	public Long createList(String name, String portalId) throws HubSpotException {
		String url = "/contacts/v1/lists";

		String properties = new JSONObject()
				.put("name", name)
				.put("dynamic", false)
				.put("portalId", portalId)
				.toString();

		try {
			JsonNode jsonNode = httpService.postRequest(url, properties);
			return jsonNode.getObject().getLong("listId");
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot create list: " + name + ". Reason: " + e.getMessage(), e);
		}
	}

	public void deleteList(String listId) throws HubSpotException {
		String url = "/contacts/v1/lists/" + listId;

		try {
			JsonNode jsonNode = httpService.deleteRequest(url);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot delete list: " + listId + ". Reason: " + e.getMessage(), e);
		}
	}

	public void assignList(Long listId, Long contactId) throws HubSpotException {
		String url = "/contacts/v1/lists/" + listId + "/add";

		String properties = new JSONObject()
					.put("vids", new JSONArray()
					.put(contactId))
					.toString();

		try {
			httpService.postRequest(url, properties);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot assign list " + listId + ". Reason: " + e.getMessage(), e);
		}
	}

	public Long logEngagement(HSEngagement engagement) throws HubSpotException {

		String url = API_HOST + "/engagements/v1/engagements";
		JsonNode jsonNode = postRequest(url, engagement.toString());

		return jsonNode.getObject()
				.getJSONObject("engagement")
				.getLong("id");

	}

	public void logEngagement(Long contactId, String body) throws HubSpotException {

		try {
			logEngagement(new HSEngagement(HSEngagement.Type.NOTE, contactId, body));
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot log NOTE for contact " + contactId + ". Reason: " + e.getMessage(), e);
		}
	}*/
}
