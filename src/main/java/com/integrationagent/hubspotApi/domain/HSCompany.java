package com.integrationagent.hubspotApi.domain;

import com.google.common.base.Strings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: dlunev
 * Date: 4/26/16 11:34 AM
 */
public class HSCompany {


	private String name;
	private String description;
	private Long id;

	private Map<String, String> properties = new HashMap<>();

	public HSCompany setProperty(String property, String value) {
        if(value != null && !value.equals("null")){
            this.properties.put(property, value);
        }

        return this;
    }

    public String getProperty(String property) {
        return this.properties.get(property);
    }

	public String getName() {
		getProperty("name");
		return name;
	}

	public void setName(String name) {
		setProperty("name", name);
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		setProperty("description", description);
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	 public String toJsonString() {
        return toJson().toString();
    }

    public JSONObject toJson() {
        Map<String, String> properties = this.properties.entrySet().stream()
                                                        .collect(Collectors.toMap(
		                                                        p -> p.getKey(),
		                                                        p -> p.getValue()));
        return mapToJson(properties);
    }

	public static JSONObject getJsonObject(String property, Object value) {
        return new JSONObject()
                .put("name", property)
                .put("value", value);
    }

    public static void putJsonObject(JSONArray ja, String property, String value){
        if(!Strings.isNullOrEmpty(value) && !value.equals("null")){
            ja.put(getJsonObject(property, value));
        }
    }

    public static JSONObject putJsonObject(JSONObject jo, String property, Object value){
        if(!Strings.isNullOrEmpty(value + "") && !value.equals("null")){
            jo.put(property, value);
        }

        return jo;
    }

    public static String mapToJsonString(Map<String, String> map) {
        return mapToJson(map).toString();
    }

    public static JSONObject mapToJson(Map<String, String> map) {
        JSONArray ja = new JSONArray();
        map.entrySet().forEach( item ->
                        putJsonObject(ja, item.getKey(), item.getValue()));

        return new JSONObject().put("properties", ja);
    }


}
