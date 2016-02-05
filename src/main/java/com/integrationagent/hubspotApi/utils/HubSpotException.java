package com.integrationagent.hubspotApi.utils;

/**
 * Author: dlunev
 * Date: 7/27/15 9:43 AM
 */
public class HubSpotException extends Exception {

	private int code;

	private String rawMessage;

	public HubSpotException(String message) {
		super(message);
	}

	public HubSpotException(String message, Throwable cause) {
		super(message, cause);
	}

	public HubSpotException(String message, int code) {
		super(message);
		this.code = code;
	}

	public HubSpotException(String message, String rawMessage) {
		super(message);
		this.rawMessage = rawMessage;
	}

	public int getCode() {
		return code;
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}
}
