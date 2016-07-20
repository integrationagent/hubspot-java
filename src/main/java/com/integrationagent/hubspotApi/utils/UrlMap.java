package com.integrationagent.hubspotApi.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: dlunev
 * Date: 7/28/15 9:12 AM
 */
public class UrlMap {

	private HashMap<String, Object> params = new HashMap<>();

	public UrlMap put(String key, Object val){
		return put(key, val, false);
	}

	public UrlMap putDuplicate(String key, Object val){
		return put(key, val, true);
	}

	private UrlMap put(String key, Object val, boolean allowDuplicates){

		if(allowDuplicates){
			List<Object> list = params.containsKey(key) ? (List<Object>) params.get(key)
					: new ArrayList<Object>();

			list.add(val);
			params.put(key, list);
		}else{
			params.put(key, val);
		}

		return this;
	}

	public Object get(String key){
		return params.get(key);
	}

	@Override
	public String toString() {
		return params.keySet().stream()
				.map(k -> {

					Object val = params.get(k);

					if (val instanceof List) {
						return ((List)val).stream().map(
								v -> k + "=" + urlEncode(v)
						).collect(Collectors.joining("&"))
								.toString();
					}

					return k + "=" + urlEncode(val);

				}).collect(Collectors.joining("&"));
	}

	public static String urlEncode(Object param){
		try {
			return URLEncoder.encode(
							param + "", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}


}
