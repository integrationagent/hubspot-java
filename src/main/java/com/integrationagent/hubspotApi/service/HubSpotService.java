package com.integrationagent.hubspotApi.service;

import com.google.common.base.Strings;
import com.integrationagent.hubspotApi.domain.Contact;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Set;

/**
 * Author: dlunev
 * Date: 2/18/15 9:15 AM
 */


public class HubSpotService {

	private String API_KEY;
	private String API_HOST;

	public HubSpotService(String api_key, String api_host) {
		this.API_KEY = api_key;
		this.API_HOST = api_host;
	}

	public Contact getContact(String email) throws HubSpotException{
		String url = "/contacts/v1/contact/email/" + email + "/profile";

		try {
			JsonNode jsonBody = getRequest(url);
			return parseContactData(jsonBody);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot get contact: " + email, e);
		}
	}

	public Contact getContact(long id) throws HubSpotException{
		String url = "/contacts/v1/contact/vid/" + id + "/profile";

		try {
			JsonNode jsonBody = getRequest(url);
			return parseContactData(jsonBody);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot get contact: " + id, e);
		}
	}

	public Contact updateContact(Contact contact) throws HubSpotException {
		if (contact.getId() <= 0) {
			throw new HubSpotException("User ID must be provided");
		}

		String url = API_HOST + "/contacts/v1/contact/vid/" + contact.getId() + "/profile";
		String properties = contact.toJsonString();

		try {
			JsonNode jsonBody = postRequest(url, properties);
			return contact;
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update contact: " + contact.getId(), e);
		}
	}

	public Contact updateOrCreateContact(Contact contact) throws HubSpotException {
		if (Strings.isNullOrEmpty(contact.getEmail())) {
			throw new HubSpotException("User email must be provided");
		}

		String url = API_HOST + "/contacts/v1/contact/createOrUpdate/email/" + contact.getEmail();
		String properties = contact.toJsonString();

		try {
			JsonNode jsonBody = postRequest(url, properties);
			contact.setId(jsonBody.getObject().getLong("vid"));
			return contact;
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update or create contact: " + contact.getEmail() + " \n" + properties, e);
		}
	}

	public void deleteContact(Contact contact) throws HubSpotException {
		if (contact.getId() <= 0) {
			throw new HubSpotException("User ID must be provided");
		}
		String url = API_HOST + "/contacts/v1/contact/vid/" + contact.getId();

		try {
			JsonNode jsonBody = deleteRequest(url);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update contact: " + contact.getId(), e);
		}
	}

	public Long createList(String name, String portal_id) throws HubSpotException {
		String url = API_HOST + "/contacts/v1/lists";

		String properties = new JSONObject()
				.put("name", name)
				.put("dynamic", false)
				.put("portalId", portal_id)
				.toString();

		JsonNode jsonNode = postRequest(url, properties);

		return jsonNode.getObject().getLong("listId");
	}

	public void deleteList(String listId) throws HubSpotException {
		String url = API_HOST + "/contacts/v1/lists/" + listId;

		try {
			JsonNode jsonNode = postRequest(url, "");
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot delete list: \n" + listId, e);
		}
	}

	public JsonNode getRequest(String url) throws HubSpotException {
		try {
			HttpResponse<JsonNode> resp = Unirest
                    .get(API_HOST + url)
                    .queryString("hapikey", API_KEY)
                        .asJson();

			if(204 != resp.getStatus() && 200 != resp.getStatus()){
				throw new HubSpotException(resp.getStatusText(), resp.getStatus());
			}

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

			if(204 != resp.getStatus() && 200 != resp.getStatus()){
				throw new HubSpotException(resp.getStatusText(), resp.getStatus());
			}

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

			if(204 != resp.getStatus() && 200 != resp.getStatus()){
				throw new HubSpotException(resp.getStatusText(), resp.getStatus());
			}

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

		postRequest(url, properties);
	}

	public void logEngagement(String properties) throws HubSpotException {

		String url = API_HOST + "/engagements/v1/engagements";
		postRequest(url, properties);
	}

	public void logNote(Long contactId, String body) throws HubSpotException {

		JSONObject jsonObject = new JSONObject()
				.put("engagement", new JSONObject()
						.put("active", true)
						.put("type", "NOTE"))
				.put("associations", new JSONObject()
						.put("contactIds", new JSONArray().put(contactId)))
				.put("metadata", new JSONObject()
						.put("body", body));

		logEngagement(jsonObject.toString());
	}

	private Contact parseContactData(JsonNode jsonBody) {
		Contact contact = new Contact();

		contact.setId(jsonBody.getObject().getLong("vid"));

		JSONObject jsonProperties = jsonBody.getObject().getJSONObject("properties");

		Set<String> keys = jsonProperties.keySet();

		keys.stream().forEach( key ->
				contact.setProperty(key,
									jsonProperties.get(key) instanceof JSONObject ?
											((JSONObject) jsonProperties.get(key)).getString("value") :
											jsonProperties.get(key).toString()
									)
		);

		return contact;
	}
}
