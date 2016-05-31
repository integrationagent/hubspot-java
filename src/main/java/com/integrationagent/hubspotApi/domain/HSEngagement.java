package com.integrationagent.hubspotApi.domain;

import com.integrationagent.hubspotApi.utils.HubSpotHelper;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: dlunev
 * Date: 1/30/16 11:17 AM
 */
public class HSEngagement {

	private String body;
	private boolean active = true;
	private Long ownerId;
	private Type type;
	private Long timestamp;
	private List<Long> contactIds = new ArrayList<>();
	private List<Long> companyIds = new ArrayList<>();
	private List<Long> dealIds = new ArrayList<>();
	private List<Long> ownerIds = new ArrayList<>();

	public HSEngagement(Type type, Long contactId, String body) {
		this.body = body;
		this.type = type;
		this.contactIds.add(contactId);
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public List<Long> getContactIds() {
		return contactIds;
	}

	public void setContactIds(List<Long> contactIds) {
		this.contactIds = contactIds;
	}

	public List<Long> getCompanyIds() {
		return companyIds;
	}

	public void setCompanyIds(List<Long> companyIds) {
		this.companyIds = companyIds;
	}

	public List<Long> getDealIds() {
		return dealIds;
	}

	public void setDealIds(List<Long> dealIds) {
		this.dealIds = dealIds;
	}

	public List<Long> getOwnerIds() {
		return ownerIds;
	}

	public void setOwnerIds(List<Long> ownerIds) {
		this.ownerIds = ownerIds;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public JSONObject toJson(){

		JSONObject engagement = new JSONObject();
		HubSpotHelper.putJsonObject(engagement, "type", type.toString());
		HubSpotHelper.putJsonObject(engagement, "active", active + "");
		HubSpotHelper.putJsonObject(engagement, "ownerId", ownerId + "");
		HubSpotHelper.putJsonObject(engagement, "timestamp", timestamp + "");

		JSONObject associations = new JSONObject();
		HubSpotHelper.putJsonObject(associations, "contactIds", contactIds);
		HubSpotHelper.putJsonObject(associations, "companyIds", companyIds);
		HubSpotHelper.putJsonObject(associations, "dealIds", dealIds);
		HubSpotHelper.putJsonObject(associations, "ownerIds", ownerIds);

		JSONObject metadata = new JSONObject();
		HubSpotHelper.putJsonObject(metadata, "body", body);

		return new JSONObject()
				.put("engagement", engagement)
				.put("associations", associations)
				.put("metadata", metadata);
	}

	public static String formatList(List<Long> list){
		return list.stream()
				.map(p -> "\"" + p + "\"")
				.collect(Collectors.joining(", ", "[", "]"));
	}

	public enum Type {
		EMAIL, CALL, MEETING, TASK, NOTE
	}
}
