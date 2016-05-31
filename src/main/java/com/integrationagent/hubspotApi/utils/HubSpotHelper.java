/*
 * Copyright:
 *
 *  developer: Sergei Dubinin
 *  e-mail: sdubininit@gmail.com
 *  date: 14.10.2015 9:58
 *  
 *  copyright (c) integrationagent.com
 */

package com.integrationagent.hubspotApi.utils;

import com.google.common.base.Strings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class HubSpotHelper {

    public static JSONObject getJsonObject(String property, Object value) {
        return new JSONObject()
                .put("property", property)
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

    public static Properties loadProperties() throws IOException {
        Properties p = new Properties();
        p.load(new FileReader(new File("src//test//resources//config.properties")));
        return p;
    }

    public static String loadPropertyValue(String propertyName) throws IOException {
        return loadProperties().getProperty(propertyName);
    }
}
