package com.integrationagent.hubspotApi;

import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.service.HubSpotService;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import org.hamcrest.core.StringContains;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class HubSpotServiceTest {

    private static String API_KEY;
    private static Long PORTAL_ID;

    private final String testEmail = "test" + Instant.now().getEpochSecond() + "@mail.com";
    private final String testBadEmail = "test@test.test";
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";

    private HubSpotService hubSpotService = new HubSpotService(API_KEY);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void oneTimeSetUp() {
        Properties p = new Properties();
        try {
            p.load(new FileReader(new File("src//test//resources//config.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        API_KEY = p.getProperty("hubspot.apikey");
        PORTAL_ID = Long.parseLong(p.getProperty("hubspot.portalid"));
    }

    @Test
    public void createContact_Test() throws Exception {
        HSContact HSContact = new HSContact(testEmail, testFirstname, testLastname);
        HSContact = hubSpotService.createContact(HSContact);
        assertNotEquals(0, HSContact.getId());
        assertEquals(HSContact.getEmail(), hubSpotService.getContact(HSContact.getEmail()).getEmail());
        hubSpotService.deleteContact(HSContact);
    }

    @Test
    public void getContact_Email_Test() throws Exception {
        HSContact HSContact = hubSpotService.getContact("denis@reviewtogo.com");
        assertEquals(79, HSContact.getId());
        assertEquals("Garry", HSContact.getFirstname());
    }

    @Test
    public void getContact_Email_Not_Found_Test() throws Exception {
        String testEmail = "zzzdenis@reviewtogo.com";
        assertNull(hubSpotService.getContact(testEmail));
    }

    @Test
    public void getContact_Id_Test() throws Exception {
        HSContact HSContact = hubSpotService.getContact(79);
        assertEquals(79, HSContact.getId());
        assertEquals("Garry", HSContact.getFirstname());
    }

    @Test
    public void getContact_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertNull(hubSpotService.getContact(id));
    }

    @Test
    public void updateOrCreateContact_Test() throws Exception {
        HSContact HSContact = new HSContact(testEmail, testFirstname, testLastname);
        HSContact = hubSpotService.updateOrCreateContact(HSContact);
        assertEquals(testFirstname, hubSpotService.getContact(HSContact.getId()).getFirstname());
    }

    @Test
    public void updateOrCreateContact_Bad_Email_Test() throws Exception {
        HSContact HSContact = new HSContact(testBadEmail, testFirstname, testLastname);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("Property values were not valid"));
        hubSpotService.updateOrCreateContact(HSContact);
    }

    @Test
    public void updateContact_Test() throws Exception {
        String test_property = "linkedinbio";
        String test_value_1 = "Test value 1";
        String test_value_2 = "Test value 2";
        String test_value;

        HSContact old_HS_contact = hubSpotService.getContact(79);

        if (old_HS_contact.getProperty(test_property).equals(test_value_1)) {
            test_value = test_value_2;
        } else {
            test_value = test_value_1;
        }

        HSContact new_HS_contact = new HSContact();
        new_HS_contact.setId(79).setEmail("denis@reviewtogo.com").setFirstname("Garry").setLastname("Vowr").setProperty(test_property, test_value);
        hubSpotService.updateContact(new_HS_contact);
        assertEquals(hubSpotService.getContact("denis@reviewtogo.com").getProperty(test_property), test_value);
    }

    @Test
    public void updateContact_Bad_Email_Test() throws Exception {
        HSContact HSContact = new HSContact(testBadEmail, testFirstname, testLastname).setId(79);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("is invalid"));
        hubSpotService.updateContact(HSContact);
    }

    @Test
    public void updateContact_Not_Found_Test() throws Exception {
        HSContact HSContact = new HSContact(testBadEmail, testFirstname, testLastname).setId(-777);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("resource not found"));
        hubSpotService.updateContact(HSContact);
    }

    @Test
    public void deleteContact_Test() throws Exception {
        HSContact HSContact = new HSContact(testEmail, testFirstname, testLastname);
        HSContact = hubSpotService.updateOrCreateContact(HSContact);
        hubSpotService.deleteContact(HSContact);

        assertNull(hubSpotService.getContact(HSContact.getId()));
    }

    @Test
    public void deleteContact_Not_Found_Test() throws Exception {
        long id= -777;
        HSContact HSContact = new HSContact().setId(id);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("resource not found"));
        hubSpotService.deleteContact(HSContact);
    }

    @Test
    public void deleteContact_No_ID_Test() throws Exception {
        HSContact HSContact = new HSContact().setEmail(testEmail);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("User ID must be provided"));
        hubSpotService.deleteContact(HSContact);
    }

    @Test
    public void getList_Test() throws Exception {
        Long listId = 1L;
        assertEquals(listId,hubSpotService.getList(listId.toString()));
    }

    @Test
    public void createList_Test() throws Exception {
        String name = "TEST_LIST2";

        Long result = hubSpotService.createList(name, PORTAL_ID.toString());
        assertEquals(result, hubSpotService.getList(result.toString()));
        hubSpotService.deleteList(result.toString());
    }

    @Test
    public void createList_Test2() throws Exception {
        String name = "TEST_LIST2";

        List<HSContact> HSContacts = new ArrayList<>();

        HSContacts.add(new HSContact("email1@dom.com", "Garry", "Pole"));
        HSContacts.add(new HSContact("email2@dom.com", "Jerry", "Stroman"));
        HSContacts.add(new HSContact("email3@dom.com", "Fill", "Jerricson"));

        hubSpotService.updateOrCreateContacts(HSContacts);

    }


}
