package com.integrationagent.hubspotApi.integraion;

import com.integrationagent.hubspotApi.service.HubSpot;
import com.integrationagent.hubspotApi.utils.Helper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HSListServiceIT {

    private HubSpot hubSpot = new HubSpot(Helper.getProperty("hubspot.apikey"));
    private String PORTAL_ID = Helper.getProperty("hubspot.portalid");

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getList_Test() throws Exception {
        Long listId = 1L;
        assertEquals(listId,hubSpot.list().getByID(listId.toString()));
    }

    @Test
    public void getList_Not_Found_Test() throws Exception {
        assertNull(hubSpot.list().getByID("fakeId"));
    }

    @Test
    public void createList_Test() throws Exception {
        String name = "TEST_LIST2";

        Long result = hubSpot.list().create(name, PORTAL_ID);
        assertEquals(result, hubSpot.list().getByID(result.toString()));
        hubSpot.list().delete(result.toString());
    }
}
