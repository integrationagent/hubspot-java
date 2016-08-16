package com.integrationagent.hubspotApi.integration;

import com.integrationagent.hubspotApi.domain.HSCompany;
import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.service.HubSpot;
import com.integrationagent.hubspotApi.utils.Helper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HSCompanyServiceIT {

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
    public void createCompany_Test() throws Exception {
        HSCompany company = new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), "test description");
        company = hubSpot.company().create(company);
        assertNotNull(company.getId());
    }

    @Test
    public void addContact_Test() throws Exception {
        HSCompany company = hubSpot.company().create(new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), "test description"));
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname));
        Thread.sleep(5000);
        hubSpot.company().addContact(contact.getId(), company.getId());
    }

    @Test
    @Ignore
    public void getByDomain_Test() throws Exception {
        List<HSCompany> companies = hubSpot.company().getByDomain("Domain");
        assertTrue(companies.size() > 0);
    }

    @Test
    public void updateCompany_Test() throws Exception {
        HSCompany company = hubSpot.company().create(new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), "test description"));
        company.setDescription("Amazing description");
        Thread.sleep(5000);
        HSCompany updatedCompany = hubSpot.company().update(company);

        assertEquals(company.getDescription(), updatedCompany.getDescription());
    }

}
