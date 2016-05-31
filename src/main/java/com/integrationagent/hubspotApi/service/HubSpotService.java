package com.integrationagent.hubspotApi.service;

/**
 * Author: dlunev
 * Date: 2/18/15 9:15 AM
 */


public class HubSpotService {

	/*private String API_KEY;
	private String PORTAL_ID;
	private static final String API_HOST = "https://api.hubapi.com";

	public HubSpotService(String api_key) {
		this.API_KEY = api_key;
	}

	public HubSpotService(String api_key, String portalId) {
		this.API_KEY = api_key;
		this.PORTAL_ID = portalId;
	}

	public Contact getContact(String email) throws HubSpotException{
		String url = "/contacts/v1/contact/email/" + email + "/profile";

		try {
			JsonNode jsonBody = getRequest(url);
			return parseContactData(jsonBody);
		} catch (HubSpotException e) {
			if (e.getCode() == 404) {
				return null;
			} else {
				throw new HubSpotException("Cannot get contact: " + email + ". Reason: " + e.getMessage(), e);
			}
		}
	}

	public Contact getContact(long id) throws HubSpotException{
		String url = "/contacts/v1/contact/vid/" + id + "/profile";

		try {
			JsonNode jsonBody = getRequest(url);
			return parseContactData(jsonBody);
		} catch (HubSpotException e) {
			if (e.getCode() == 404) {
				return null;
			} else {
				throw new HubSpotException("Cannot get contact: " + id + ". Reason: " + e.getMessage(), e);
			}
		}
	}

	public Contact createContact(Contact contact) throws HubSpotException {
		if (Strings.isNullOrEmpty(contact.getEmail())) {
			throw new HubSpotException("User email must be provided");
		}

		String url = API_HOST + "/contacts/v1/contact";
		String properties = contact.toJsonString();

		try {
			JsonNode jsonBody = postRequest(url, properties);
			contact.setId(jsonBody.getObject().getLong("vid"));
			return contact;
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot create contact: " + contact.getEmail() + ". Reason: " + e.getMessage(), e);
		}
	}

	public Contact updateContact(Contact contact) throws HubSpotException {
		if (contact.getId() == 0) {
			throw new HubSpotException("User ID must be provided");
		}

		String url = API_HOST + "/contacts/v1/contact/vid/" + contact.getId() + "/profile";
		String properties = contact.toJsonString();

		try {
			JsonNode jsonBody = postRequest(url, properties);
			return contact;
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update contact: " + contact.getId() + ". Reason: " + e.getMessage(), e);
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
			throw new HubSpotException("Cannot update or create contact: " + contact.getEmail() + ". Reason: " + e.getMessage(), e);
		}
	}


	public Company createCompany(Company company) throws HubSpotException {

		String url = API_HOST + "/companies/v2/companies/";
		String properties = company.toJsonString();

		try {
			JsonNode jsonBody = postRequest(url, properties);
			company.setId(jsonBody.getObject().getLong("companyId"));
			return company;
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot create company: " + company + ". Reason: " + e.getMessage(), e);
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

	public void updateOrCreateContacts(List<Contact> contacts) throws HubSpotException {

		String url = API_HOST + "/contacts/v1/contact/batch/";

		JSONArray array = new JSONArray();

		for (Contact contact : contacts) {
			JSONObject jsonObject = contact.toJson();
			jsonObject.put("email", contact.getEmail());
			array.put(jsonObject);
		}

		postRequest(url, array.toString());
	}

	public void deleteContact(Contact contact) throws HubSpotException {
		if (contact.getId() == 0) {
			throw new HubSpotException("User ID must be provided");
		}
		String url = API_HOST + "/contacts/v1/contact/vid/" + contact.getId();

		try {
			JsonNode jsonBody = deleteRequest(url);
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot update contact: " + contact.getId() + ". Reason: " + e.getMessage(), e);
		}
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

	public Long logEngagement(Engagement engagement) throws HubSpotException {

		String url = API_HOST + "/engagements/v1/engagements";
		JsonNode jsonNode = postRequest(url, engagement.toString());

		return jsonNode.getObject()
				.getJSONObject("engagement")
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
			logEngagement(new Engagement(Engagement.Type.NOTE, contactId, body));
		} catch (HubSpotException e) {
			throw new HubSpotException("Cannot log NOTE for contact " + contactId + ". Reason: " + e.getMessage(), e);
		}
	}
	*/


}
