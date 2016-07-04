package com.integrationagent.hubspotApi.integraion;

import com.integrationagent.hubspotApi.domain.HSForm;
import com.integrationagent.hubspotApi.service.HubSpot;
import com.integrationagent.hubspotApi.utils.Helper;
import com.integrationagent.hubspotApi.utils.UrlMap;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class HSFormServiceIT {

    private HubSpot hubSpot = new HubSpot(Helper.getProperty("hubspot.apikey"));

    @Test
    public void getAll_Test() throws Exception {
        List<HSForm> forms = hubSpot.form().getAll();
        assertNotNull(forms);
    }

    @Test
    @Ignore
    public void submit_Test() throws Exception {
        String formId = "form ID";
        UrlMap properties = new UrlMap();
        String PortalId = "portal ID";
        hubSpot.form().submit(formId, properties, PortalId);
    }
}
