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

import java.util.Map;

public class HubSpotHelper {

    public static JSONObject getJsonObject(String property, Object value) {
        return new JSONObject()
                .put("property", property)
                .put("value", value);
    }

    public static void putJsonObject(JSONArray ja, String property, String value){
        if(!Strings.isNullOrEmpty(value)){
            ja.put(getJsonObject(property, value));
        }
    }

    public static String mapToJsonString(Map<String, String> map) {
        JSONArray ja = new JSONArray();
        map.entrySet().forEach( item ->
                        putJsonObject(ja, item.getKey(), item.getValue()));

        return new JSONObject().put("properties", ja).toString();
    }

    public static String getHubSpotStatus(String unsubscribeReason, String status) {
        String result;

        if (status.equals("subscribed") && Strings.isNullOrEmpty(unsubscribeReason)) {
            result = "s_Active";
        } else {
            switch (unsubscribeReason) {
                case "admin_unsubscribed":
                    result = "s_Refunded";
                    break;
                case "auto_expired":
                    result = "s_Expired";
                    break;
                case "billing_failed":
                    result = "s_billing_failed";
                    break;
                case "client_cancelled":
                    result = "s_Unsubscribed";
                    break;
                default:
                    result = "";
                    break;
            }
        }
        return result;
    }
}
