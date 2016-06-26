package com.integrationagent.hubspotApi.service;

import com.integrationagent.hubspotApi.utils.HubSpotException;

public class HSWorkflowService {

    private HttpService httpService;

    public HSWorkflowService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void enroll(String workflowId, String email) throws HubSpotException {
		String url = "https://api.hubapi.com/automation/v2/workflows/" + workflowId + "/enrollments/contacts/" + email;
		httpService.postRequest(url, "");
	}
}
