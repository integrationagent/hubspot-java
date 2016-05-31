package com.integrationagent.hubspotApi;

import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.service.HubSpotService;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import com.integrationagent.hubspotApi.utils.HubSpotHelper;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

import static org.junit.Assert.*;

public class HubSpotServiceTest {
    private final String testEmail = "test" + Instant.now().getEpochSecond() + "@mail.com";
    private final String testBadEmail = "test@test.test";
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void oneTimeSetUp() throws IOException {
        Properties p = new Properties();
        try {
            p.load(new FileReader(new File("src//test//resources//config.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HubSpot.apiKey = p.getProperty("hubspot.apikey");
    }

    @Test
    public void createContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail, testFirstname, testLastname);
        contact = HSContact.create(contact);
        assertNotEquals(0, contact.getId());
        assertEquals(contact.getEmail(), HSContact.retrieveByEmail(contact.getEmail()).getEmail());
        HSContact.delete(contact);
    }

    @Test
    public void getContact_Email_Test() throws Exception {
        HSContact contact = HSContact.retrieveByEmail("denis@reviewtogo.com");
        assertEquals(79, contact.getId());
        assertEquals("Garry", contact.getFirstname());
    }

    /*@Test
    public void getContact_Email_Not_Found_Test() throws Exception {
        String testEmail = "zzzdenis@reviewtogo.com";
        assertNull(hubSpotService.getContactByEmail(testEmail));
    }

    @Test
    public void getContact_Id_Test() throws Exception {
        HSContact contact = hubSpotService.getContactById(79);
        assertEquals(79, contact.getId());
        assertEquals("Garry", contact.getFirstname());
    }

    @Test
    public void getContact_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertNull(hubSpotService.getContactById(id));
    }

    @Test
    public void updateOrCreateContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail, testFirstname, testLastname);
        contact = hubSpotService.updateOrCreateContact(contact);
        assertEquals(testFirstname, hubSpotService.getContactById(contact.getId()).getFirstname());
    }

    @Test
    public void updateOrCreateContact_Bad_Email_Test() throws Exception {
        HSContact contact = new HSContact(testEmail, testFirstname, testLastname);

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

        HSContact old_contact = hubSpotService.getContactById(79);

        if (old_contact.getProperty(test_property).equals(test_value_1)) {
            test_value = test_value_2;
        } else {
            test_value = test_value_1;
        }

        HSContact new_contact = new HSContact();
        new_contact.setId(79).setEmail("denis@reviewtogo.com").setFirstname("Garry").setLastname("Vowr").setProperty(test_property, test_value);
        hubSpotService.updateContact(new_contact);
        assertEquals(hubSpotService.getContactByEmail("denis@reviewtogo.com").getProperty(test_property), test_value);
    }

    @Test
    public void updateContact_Bad_Email_Test() throws Exception {
        HSContact contact = new HSContact(testBadEmail, testFirstname, testLastname).setId(79);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("is invalid"));
        hubSpotService.updateContact(contact);
    }

    @Test
    public void updateContact_Not_Found_Test() throws Exception {
        HSContact contact = new HSContact(testBadEmail, testFirstname, testLastname).setId(-777);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("resource not found"));
        hubSpotService.updateContact(contact);
    }

    @Test
    public void deleteContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail, testFirstname, testLastname);
        contact = hubSpotService.updateOrCreateContact(contact);
        hubSpotService.deleteContact(contact);

        assertNull(hubSpotService.getContactById(contact.getId()));
    }

    @Test
    public void deleteContact_Not_Found_Test() throws Exception {
        long id= -777;
        HSContact contact = new HSContact().setId(id);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("resource not found"));
        hubSpotService.deleteContact(contact);
    }

    @Test
    public void deleteContact_No_ID_Test() throws Exception {
        HSContact contact = new HSContact().setEmail(testEmail);

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
        String name = "TEST_LIST2";

        Long result = hubSpotService.createList(name, HubSpotHelper.loadPropertyValue("hubspot.portalid"));
        assertEquals(result, hubSpotService.getList(result.toString()));
        hubSpotService.deleteList(result.toString());
    }

    @Test
    public void createList_Test2() throws Exception {
        String name = "TEST_LIST2";

        List<Contact> contacts = new ArrayList<>();

        contacts.add(new Contact("email1@dom.com", "Garry", "Pole"));
        contacts.add(new Contact("email2@dom.com", "Jerry", "Stroman"));
        contacts.add(new Contact("email3@dom.com", "Fill", "Jerricson"));

        hubSpotService.updateOrCreateContacts(contacts);

    }
*/

}
