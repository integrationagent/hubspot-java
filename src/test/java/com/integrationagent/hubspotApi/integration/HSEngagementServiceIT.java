package com.integrationagent.hubspotApi.integration;

import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.service.HubSpot;
import com.integrationagent.hubspotApi.utils.Helper;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertNotNull;

public class HSEngagementServiceIT {

    private String testEmail1;
    private final String testBadEmail = "test@test.test";
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";

    private HubSpot hubSpot = new HubSpot(Helper.getProperty("hubspot.apikey"));

    @Before
    public void setUp() throws Exception {
        testEmail1 = "test1" + Instant.now().getEpochSecond() + "@mail.com";
    }

    @Test
    public void create_Test() throws Exception {
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname));
        Thread.sleep(5000);
        assertNotNull(hubSpot.engagement().create(contact.getId(), "Creating engagement"));
    }

    @Test
    public void delete_Test() throws Exception {
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname));
        Thread.sleep(5000);
        Long id = hubSpot.engagement().create(contact.getId(), "Creating engagement");
        Thread.sleep(5000);
        hubSpot.engagement().delete(id);
    }
}
