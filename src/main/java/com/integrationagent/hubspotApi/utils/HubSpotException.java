package com.integrationagent.hubspotApi.utils;

/**
 * Author: dlunev
 * Date: 7/27/15 9:43 AM
 */
public class HubSpotException extends Exception {

	public HubSpotException(String message) {
		super(message);
	}

	public HubSpotException(String message, Throwable cause) {
		super(message, cause);
	}
}
