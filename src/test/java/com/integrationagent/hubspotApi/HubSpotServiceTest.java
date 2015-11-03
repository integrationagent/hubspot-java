package com.integrationagent.hubspotApi;

import com.integrationagent.hubspotApi.domain.Contact;
import com.integrationagent.hubspotApi.service.HubSpotService;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
    public void updateOrCreateContact_Test() throws Exception {
        String testEmail = "test@mail.ru";
        String testFirstname = "Testfristname";
        String testLastname = "Testlastname";

        Contact contact = new Contact(testEmail, testFirstname, testLastname);
        contact = hubSpotService.updateOrCreateContact(contact);
        assertEquals(testFirstname, hubSpotService.getContact(contact.getId()).getFirstname());
    }

    @Test
    public void updateContact_Test() throws Exception {
        String test_property = "linkedinbio";
        String test_value_1 = "Test value 1";
        String test_value_2 = "Test value 2";
        String test_value;


        Contact old_contact = hubSpotService.getContact(79);

        if (old_contact.getProperty(test_property).equals(test_value_1)) {
            test_value = test_value_2;
        } else {
            test_value = test_value_1;
        }

        Contact new_contact = new Contact();
        new_contact.setId(79).setEmail("denis@reviewtogo.com").setFirstname("Garry").setLastname("Vowr").setProperty(test_property, test_value);
        hubSpotService.updateContact(new_contact);
        assertEquals(hubSpotService.getContact("denis@reviewtogo.com").getProperty(test_property), test_value);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void deleteContact_Test() throws Exception {
        String testEmail = "test@mail.ru";
        String testFirstname = "Testfristname";
        String testLastname = "Testlastname";

        Contact contact = new Contact(testEmail, testFirstname, testLastname);
        contact = hubSpotService.updateOrCreateContact(contact);
        hubSpotService.deleteContact(contact);

        exception.expect(HubSpotException.class);
        hubSpotService.getContact(contact.getId());
    }
}
