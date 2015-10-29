package com.integrationagent.hubspotApi.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;
import com.integrationagent.cco.model.ContactProperty;
import com.integrationagent.hubspotApi.domain.Contact;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

	public JsonNode getCustomerRawDataByEmail(String email) {
		JsonNode jsonBody = null;
		try {
			jsonBody = getRequest("/contacts/v1/contact/email/" + email + "/profile");
		} catch (UnirestException e) {
			log.error("Cannot get contact: " + email, e);
		}

		if(jsonBody == null || !jsonBody.getObject().has("vid")){
			return null;
		} else {
			return jsonBody;
		}
	}

	public Contact getCustomerByEmail(String email){

		try {
			JsonNode jsonBody = getRequest("/contacts/v1/contact/email/" + email + "/profile");

			if(jsonBody == null || !jsonBody.getObject().has("vid")){
				return null;
			}

			Contact contact = new Contact();
			contact.setId(jsonBody.getObject().getLong("vid"));

			JSONObject jsonProperties = jsonBody.getObject().getJSONObject("properties");

			if(jsonProperties.has("firstname")){
				contact.setFirstName(
						jsonProperties.getJSONObject("firstname").getString("value"));
			}

			if(jsonProperties.has("lastname")){
				contact.setLastName(
						jsonProperties.getJSONObject("lastname").getString("value"));
			}

			if(jsonProperties.has("high_level_alert_note")){
				contact.setHighLevelAlertNote(
						jsonProperties.getJSONObject("high_level_alert_note").getString("value"));
			}

			List<ContactProperty> courseProperties = getContactProperties("course_status");
			List<ContactProperty> courseFields = assignProperties(courseProperties, jsonProperties);

			Comparator<ContactProperty> notEmptyFirst = (p1, p2) ->
					(p1.getValue().isEmpty() == p2.getValue().isEmpty()
							? 0 : (p1.getValue().isEmpty() ? 1 : -1));

		    courseFields = courseFields.stream().sorted(
				    notEmptyFirst.thenComparing(ContactProperty::getLabel))
				    .collect(Collectors.toList());

			List<ContactProperty> certificationProperties = getContactProperties("certifications");
			List<ContactProperty> certificationFields = assignProperties(certificationProperties, jsonProperties);

			certificationFields = certificationFields.stream().filter(
					p -> Arrays.asList("which_certifications_do_you_hold",
							"which_credential_do_you_want_",
							"upcoming_exam_date",
							"upcoming_exam_for")
									.contains(p.getName())
			).collect(Collectors.toList());


			List<ContactProperty> supportProperties = getContactProperties("_support");
			List<ContactProperty> supportFields = assignProperties(supportProperties, jsonProperties);

			supportFields = supportFields.stream().filter(
					p -> Arrays.asList("products_purchases",
							"coach",
							"upcoming_exam_date",
							"upcoming_exam_for")
									.contains(p.getName())
			).collect(Collectors.toList());

			supportFields.addAll(certificationFields);
			supportFields.addAll(courseFields);

			contact.setCourseStatusProperties(supportFields);

			return contact;

		} catch (HubSpotException e) {
			log.error("Cannot get contact: " + email, e);
		}

		return null;

	}

	public void updateContact(String id, String properties) throws HubSpotException {

		String url = API_HOST + "/contacts/v1/contact/vid/" + id + "/profile";

		try {
			JsonNode result = postRequest(url, properties);

		} catch (HubSpotException e) {
			switch (e.getCode()) {
				case 400:
					throw new HubSpotException("Property doesn't exist, or the property value is invalid: " + id + " \n" + properties, e);
				case 401:
					throw new HubSpotException("An unauthorized request is made", e);
				case 500:
					throw new HubSpotException("Internal server error", e);
				default:
					throw new HubSpotException("Cannot update contact: " + id + " \n" + properties, e);
			}

		}
	}

	public Long updateContactByEmail(String email, String properties) throws HubSpotException {

		String url = API_HOST + "/contacts/v1/contact/createOrUpdate/email/" + email;

		try {

			JsonNode result = postRequest(url, properties);

			return result.getObject().getLong("vid");

		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update contact: " + email + " \n"
					+ properties, e);
		}
	}

	public void deleteContact(String id) {
		String url = API_HOST + "/contacts/v1/contact/vid/" + id;
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

	public List<ContactProperty> getContactProperties(String group){

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
	}

	public List<ContactProperty> assignProperties(List<ContactProperty> hbProperties, JSONObject jsonProperties){


		List<ContactProperty> contactProperties = new ArrayList<>();

			for (ContactProperty hbProperty : hbProperties) {

				ContactProperty contactProperty = hbProperty;

				String value = "";

				if(jsonProperties.has(hbProperty.getName())){

					value = jsonProperties.getJSONObject(hbProperty.getName()).getString("value");

					if(!Strings.isNullOrEmpty(value) && hbProperty.getFieldType().equals("date")){
						Instant instant = Instant.ofEpochMilli(Long.parseLong(value));
                        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
						value = ldt.format(DateTimeFormatter.ofPattern("MM/dd/yy"));
					}
				}

				if(hbProperty.getName().contains("_date")){
					contactProperty.setFieldType("date");
				}

				contactProperty.setValue(value);
				contactProperties.add(contactProperty);
			}

		return contactProperties;
	}

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
			throw new HubSpotException("Cannot make a request: \n" + properties, e);
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
