package com.integrationagent.hubspotApi.integraion;

import com.integrationagent.hubspotApi.service.HubSpot;
import com.integrationagent.hubspotApi.utils.Helper;
import org.junit.Ignore;
import org.junit.Test;

public class HSWorkflowServiceIT {

    private HubSpot hubSpot = new HubSpot(Helper.getProperty("hubspot.apikey"));

    @Test
    @Ignore
    public void enroll_Test() throws Exception {
        String workflowId = "workflow ID";
        String email = "email";
        hubSpot.workflow().enroll(workflowId, email);
    }
}
