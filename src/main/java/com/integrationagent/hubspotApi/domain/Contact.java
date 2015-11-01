package com.integrationagent.hubspotApi.domain;

import com.integrationagent.hubspotApi.utils.HubSpotHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Contact {
    private long id;
    private String email;
    private String firstname;
    private String lastname;
    private Map<String, String> properties = new HashMap<String, String>();

    public Contact() {
    }

    public Contact(long id, String email, String firstname, String lastname) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Contact(long id, String email, String firstname, String lastname, Map<String, String> properties) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.properties = properties;
    }

    public long getId() {
        return id;
    }

    public Contact setId(long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Contact setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public Contact setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public Contact setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Contact setProperties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    public Contact setProperty(String property, String value) {
        this.properties.put(property, value);
        return this;
    }

    public String getProperty(String property) {
        return this.properties.get(property);
    }

    public String toJsonString() {

        JSONArray ja = new JSONArray();
            ja.put(HubSpotHelper.getJsonObject("firstname", this.getFirstname()));
            ja.put(HubSpotHelper.getJsonObject("lastname", this.getLastname()));
            ja.put(HubSpotHelper.getJsonObject("email", this.getEmail()));


        Iterator iterator = this.getProperties().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            String key = (String)pair.getKey();
            String value = (String)pair.getValue();

            ja.put(HubSpotHelper.getJsonObject(key,value));
        }

        return new JSONObject().put("properties", ja).toString();
    }
}
