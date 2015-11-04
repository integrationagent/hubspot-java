package com.integrationagent.hubspotApi.domain;

import com.google.common.base.Strings;
import com.integrationagent.hubspotApi.utils.HubSpotHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Contact {

    private Map<String, String> properties = new HashMap<>();

    public Contact() {
    }

    public Contact(String email, String firstname, String lastname) {
        this.properties.put("email", email);
        this.properties.put("firstname", firstname);
        this.properties.put("lastname", lastname);
    }

    public Contact(long id, String email, String firstname, String lastname, Map<String, String> properties) {
        this.properties.put("vid", Long.toString(id));
        this.properties.put("email", email);
        this.properties.put("firstname", firstname);
        this.properties.put("lastname", lastname);
        this.properties.putAll(properties);
    }

    public long getId() {
        return !Strings.isNullOrEmpty(this.properties.get("vid")) ? Long.parseLong(this.properties.get("vid")) : 0;
    }

    public Contact setId(long id) {
        this.properties.put("vid", Long.toString(id));
        return this;
    }

    public String getEmail() {
        return this.properties.get("email");
    }

    public Contact setEmail(String email) {
        this.properties.put("email", email);
        return this;
    }

    public String getFirstname() {
        return this.properties.get("firstname");
    }

    public Contact setFirstname(String firstname) {
        this.properties.put("firstname", firstname);
        return this;
    }

    public String getLastname() {
        return this.properties.get("lastname");
    }

    public Contact setLastname(String lastname) {
        this.properties.put("lastname", lastname);
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
        Map<String, String> properties = this.properties.entrySet().stream()
                                                        .filter(p -> !p.getKey().equals("vid"))
                                                        .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        return HubSpotHelper.mapToJsonString(properties);
    }
}
