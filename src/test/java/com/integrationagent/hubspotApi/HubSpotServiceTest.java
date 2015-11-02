package com.integrationagent.hubspotApi;

import com.integrationagent.hubspotApi.domain.Contact;
import com.integrationagent.hubspotApi.service.HubSpotService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HubSpotServiceTest {

    private String API_KEY = "e84650f2-c176-4e40-a8e6-2bc416ef8ffc";
    private String API_HOST = "http://api.hubapi.com";

    private HubSpotService hubSpotService = new HubSpotService(API_KEY, API_HOST);

    @Test
    public void getContact_Email_Test() throws Exception {
        Contact contact = hubSpotService.getContact("denis@reviewtogo.com");
        assertEquals(79, contact.getId());
        assertEquals("Garry", contact.getFirstname());
    }

    @Test
    public void getContact_Id_Test() throws Exception {
        Contact contact = hubSpotService.getContact(79);
        assertEquals(79, contact.getId());
        assertEquals("Garry", contact.getFirstname());
    }

    @Test
    public void updateContact_Test() throws Exception {
        String test_property = "linkedinbio";
        String test_value = "Test value";

        Contact contact = new Contact();
        contact.setId(79).setEmail("denis@reviewtogo.com").setFirstname("Garry").setLastname("Vowr").setProperty(test_property, test_value);
        hubSpotService.updateContact(contact);
        assertEquals(hubSpotService.getContact("denis@reviewtogo.com").getProperty(test_property), test_value);
    }

}
