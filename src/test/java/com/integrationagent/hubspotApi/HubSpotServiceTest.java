package com.integrationagent.hubspotApi;

import com.integrationagent.hubspotApi.domain.Contact;
import com.integrationagent.hubspotApi.service.HubSpotService;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class HubSpotServiceTest {

    private String API_KEY = "API_KEY";
    private String API_HOST = "http://api.hubapi.com";
    private Long PORTAL_ID = 000000L;

    private HubSpotService hubSpotService = new HubSpotService(API_KEY, API_HOST);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getContact_Email_Test() throws Exception {
        Contact contact = hubSpotService.getContact("denis@reviewtogo.com");
        assertEquals(79, contact.getId());
        assertEquals("Garry", contact.getFirstname());
    }

    @Test
    public void getContact_Email_Not_Found_Test() throws Exception {
        String testEmail = "zzzdenis@reviewtogo.com";

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("contact does not exist"));
        Contact contact = hubSpotService.getContact(testEmail);
    }

    @Test
    public void getContact_Id_Test() throws Exception {
        Contact contact = hubSpotService.getContact(79);
        assertEquals(79, contact.getId());
        assertEquals("Garry", contact.getFirstname());
    }

    @Test
    public void getContact_Id_Not_Found_Test() throws Exception {
        long id = -777;

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("contact does not exist"));
        Contact contact = hubSpotService.getContact(id);
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
    public void updateOrCreateContact_Bad_Email_Test() throws Exception {
        String testEmail = "test@test.test";
        String testFirstname = "Testfristname";
        String testLastname = "Testlastname";

        Contact contact = new Contact(testEmail, testFirstname, testLastname);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("Property values were not valid"));
        hubSpotService.updateOrCreateContact(contact);
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

    @Test
    public void updateContact_Bad_Email_Test() throws Exception {
        String testEmail = "test@test.test";
        String testFirstname = "Testfristname";
        String testLastname = "Testlastname";

        Contact contact = new Contact(testEmail, testFirstname, testLastname).setId(79);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("is invalid"));
        hubSpotService.updateContact(contact);
    }

    @Test
    public void deleteContact_Test() throws Exception {
        String testEmail = "test@mail.ru";
        String testFirstname = "Testfristname";
        String testLastname = "Testlastname";

        Contact contact = new Contact(testEmail, testFirstname, testLastname);
        contact = hubSpotService.updateOrCreateContact(contact);
        hubSpotService.deleteContact(contact);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("contact does not exist"));
        hubSpotService.getContact(contact.getId());
    }

    @Test
    public void deleteContact_Not_Found_Test() throws Exception {
        long id= -777;
        Contact contact = new Contact().setId(id);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("resource not found"));
        hubSpotService.deleteContact(contact);
    }

    @Test
    public void deleteContact_No_ID_Test() throws Exception {
        String testEmail = "test@mail.ru";
        Contact contact = new Contact().setEmail(testEmail);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("User ID must be provided"));
        hubSpotService.deleteContact(contact);
    }

    @Test
    public void getList_Test() throws Exception {
        Long listId = 1L;
        assertEquals(listId,hubSpotService.getList(listId.toString()));
    }

    @Test
    public void createList_Test() throws Exception {
        Long portalId = PORTAL_ID;
        String name = "TEST_LIST2";

        Long result = hubSpotService.createList(name, portalId.toString());
        assertEquals(result, hubSpotService.getList(result.toString()));
        hubSpotService.deleteList(result.toString());
    }


}
