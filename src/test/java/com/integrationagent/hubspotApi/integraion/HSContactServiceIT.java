package com.integrationagent.hubspotApi.integraion;

import com.integrationagent.hubspotApi.utils.Helper;
import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.service.HubSpot;
import com.integrationagent.hubspotApi.utils.HubSpotException;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.Assert.*;

public class HSContactServiceIT {

    private String testEmail1;
    private String testEmail2;
    private String testEmail3;
    private final String testBadEmail = "test@test.test";
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";

    private HubSpot hubSpot = new HubSpot(Helper.getProperty("hubspot.apikey"));

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        testEmail1 = "test1" + Instant.now().getEpochSecond() + "@mail.com";
        testEmail2 = "test2" + Instant.now().getEpochSecond() + "@mail.com";
        testEmail3 = "test3" + Instant.now().getEpochSecond() + "@mail.com";
    }

    @Test
    public void createContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname);
        contact = hubSpot.contact().create(contact);
        assertNotEquals(0L, contact.getId());
        assertEquals(contact.getEmail(), hubSpot.contact().getByEmail(contact.getEmail()).getEmail());
        hubSpot.contact().delete(contact);
    }

    @Test
    public void getContact_Email_Test() throws Exception {
        long contactId = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname)).getId();
        Thread.sleep(5000);
        HSContact contact = hubSpot.contact().getByEmail(testEmail1);
        assertEquals(contactId, contact.getId());
        assertEquals(testFirstname, contact.getFirstname());
    }

    @Test
    public void getContact_Email_Not_Found_Test() throws Exception {
        String testEmail1 = "zzzdenis@reviewtogo.com";
        assertNull(hubSpot.contact().getByEmail(testEmail1));
    }

    @Test
    public void getContact_Id_Test() throws Exception {
        long contactId = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname)).getId();
        Thread.sleep(5000);
        HSContact contact = hubSpot.contact().getByID(contactId);
        assertEquals(contactId, contact.getId());
        assertEquals(testFirstname, contact.getFirstname());
    }

    @Test
    public void getContact_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertNull(hubSpot.contact().getByID(id));
    }

    @Test
    public void updateOrCreateContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname);
        contact = hubSpot.contact().updateOrCreate(contact);
        assertEquals(testFirstname, hubSpot.contact().getByID(contact.getId()).getFirstname());
    }

    @Test
    public void updateOrCreateContact_Bad_Email_Test() throws Exception {
        HSContact contact = new HSContact(testBadEmail, testFirstname, testLastname);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("Property values were not valid"));
        hubSpot.contact().updateOrCreate(contact);
    }

    @Test
    public void updateContact_Test() throws Exception {
        String test_property = "linkedinbio";
        String test_value = "Test value 1";
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname));
        Thread.sleep(5000);
        contact.setProperty(test_property, test_value);
        hubSpot.contact().update(contact);
        assertEquals(hubSpot.contact().getByID(contact.getId()).getProperty(test_property), test_value);
    }

    @Test
    public void updateContact_Bad_Email_Test() throws Exception {
        HSContact contact = new HSContact(testBadEmail, testFirstname, testLastname).setId(1);
        exception.expect(HubSpotException.class);
        hubSpot.contact().update(contact);
    }

    @Test
    public void updateContact_Not_Found_Test() throws Exception {
        HSContact contact = new HSContact(testBadEmail, testFirstname, testLastname).setId(-777);

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.contact().update(contact);
    }

    @Test
    public void deleteContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname);
        contact = hubSpot.contact().updateOrCreate(contact);
        hubSpot.contact().delete(contact);

        assertNull(hubSpot.contact().getByID(contact.getId()));
    }

    @Test
    public void deleteContact_Not_Found_Test() throws Exception {
        long id= -777;
        HSContact contact = new HSContact().setId(id);

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.contact().delete(contact);
    }

    @Test
    public void deleteContact_No_ID_Test() throws Exception {
        HSContact contact = new HSContact().setEmail(testEmail1);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("User ID must be provided"));
        hubSpot.contact().delete(contact);
    }

    @Test
    /*
    * From docs:
    * "Changes made through this endpoint are processed asynchronously,
    * so can take several minutes for changes to be applied to contact records."
    * It means we can't check updated data without any delay. As delay is not defined It's not a good idea to wait.
    * So we test only that no exception are thrown
    * */
    public void updateOrCreateContacts_Test() throws Exception {
        HSContact contact1 = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname));
        HSContact contact2 = hubSpot.contact().create(new HSContact(testEmail2, testFirstname, testLastname));
        HSContact contact3 = hubSpot.contact().create(new HSContact(testEmail3, testFirstname, testLastname));

        contact1.setFirstname("changed1");
        contact2.setFirstname("changed2");
        contact3.setFirstname("changed3");

        hubSpot.contact().updateOrCreateContacts(Arrays.asList(contact1, contact2, contact3));

        assertEquals(1, 1); //empty body is returned. Our expectations that no exceptions are thrown

        hubSpot.contact().delete(contact1);
        hubSpot.contact().delete(contact2);
        hubSpot.contact().delete(contact3);
    }
}
