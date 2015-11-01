package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.domain.Contact;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

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
		JsonNode jsonBody;
		Contact contact;

		String url = "/contacts/v1/contact/email/" + email + "/profile";

		try {
			jsonBody = getRequest(url);

			contact = new Contact();
			contact.setId(jsonBody.getObject().getLong("vid"));
			contact.setEmail(email);

			JSONObject jsonProperties = jsonBody.getObject().getJSONObject("properties");

			if(jsonProperties.has("firstname")){
				contact.setFirstname(
						jsonProperties.getJSONObject("firstname").getString("value"));
			}

			if(jsonProperties.has("lastname")){
				contact.setLastname(
						jsonProperties.getJSONObject("lastname").getString("value"));
			}

			Iterator keys = jsonProperties.keys();
			while(keys.hasNext()) {
				String key = (String)keys.next();
				String value;
				if ( jsonProperties.get(key) instanceof JSONObject ) {
					value = ((JSONObject) jsonProperties.get(key)).getString("value");
				} else {
					value = jsonProperties.get(key).toString();
				}

				contact.setProperty(key, value);
			}
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot get contact: " + email, e);
		}

		return contact;
	}

	public void updateContact(Contact contact) throws HubSpotException {

		if (contact.getId() <= 0) {
			throw new HubSpotException("User ID must be provided");
		}

		String url = API_HOST + "/contacts/v1/contact/vid/" + contact.getId() + "/profile";
		String properties = contact.toJsonString();
		JsonNode jsonBody;

		try {
			jsonBody = postRequest(url, properties);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update contact: " + contact.getId(), e);
		}
	}

	public Long updateOrCreateContact(Contact contact) throws HubSpotException {

		String url = API_HOST + "/contacts/v1/contact/createOrUpdate/email/" + contact.getEmail();

		String properties = "";

		//do JSON stuff
		//try Jackosn JSON:
		// http://stackoverflow.com/questions/15786129/converting-java-objects-to-json-with-jackson
		// http://stackoverflow.com/questions/17819710/is-there-any-way-to-convert-a-map-to-a-json-representation-using-jackson-without

		try {
			JsonNode res = postRequest(url, properties);

			return res.getObject().getLong("vid");

		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update contact: " + contact.getEmail() + " \n" + properties, e);
		}
	}

	public void deleteContact(Contact contact) throws HubSpotException {
		if (contact.getId() <= 0) {
			throw new HubSpotException("User ID must be provided");
		}
		String url = API_HOST + "/contacts/v1/contact/vid/" + contact.getId();
		JsonNode jsonBody;

		try {
			jsonBody = deleteRequest(url);
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

	//return Map<String,String>
	/*public List<ContactProperty> getContactProperties(String group){

		List<ContactProperty> properties = new ArrayList<ContactProperty>();

		try {

			JsonNode jsonBody = getRequest("/contacts/v1/groups/" + group);

			ObjectMapper mapper = new ObjectMapper();

			properties = mapper.readValue(
					jsonBody.getObject().get("properties").toString(),
					TypeFactory.defaultInstance().constructCollectionType(List.class,
							ContactProperty.class));

		} catch (Exception e) {
			log.error("Cannot get properties", e);
		}


		List<ContactProperty> hbProperties = properties.stream().filter(
					p -> (!p.isDeleted() && !p.isHidden())
			).collect(Collectors.toList());

		return hbProperties;
	}*/

	public JsonNode getRequest(String url) throws HubSpotException {

		try {
			HttpResponse<JsonNode> resp = Unirest
                    .get(API_HOST + url)
                    .queryString("hapikey", API_KEY)
                        .asJson();

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
}
