package com.integrationagent.hubspotApi.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Author: dlunev
 * Date: 8/18/15 3:27 PM
 */
public class HubSpotProperties {

	private HashMap<String, Object> params = new HashMap<>();

	public HubSpotProperties put(String key, Object val){
		params.put(key, val);
		return this;
	}

	@Override
	public String toString() {

		JSONArray ja = new JSONArray();
		params.keySet().forEach(
				e -> ja.put(new JSONObject()
								.put("property", e)
								.put("value", params.get(e))));

		JSONObject jsonObject = new JSONObject()
				.put("properties", ja);

		return jsonObject.toString();
	}
}
