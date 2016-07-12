package com.integrationagent.hubspotApi.unit;

import com.integrationagent.hubspotApi.domain.HSContact;
import com.integrationagent.hubspotApi.service.HSContactService;
import com.integrationagent.hubspotApi.service.HttpService;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HSContactJSONTest {

    HSContactService service = new HSContactService(new HttpService("key", "apiBase"));

    @Test
    public void parseContactData_Test() throws Exception {
        String inputData = "{properties:{test:1},vid:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSContact contact = service.parseContactData(jsonObject);
        assertEquals(contact.getId(),71);
        assertEquals(contact.getProperty("test"),"1");
    }

    @Test
    public void toJson_Test() throws Exception {
        String inputData = "{properties:{test:1},vid:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSContact contact = service.parseContactData(jsonObject);
        String result = contact.toJson().toString();
        assertEquals("{\"properties\":[{\"property\":\"test\",\"value\":\"1\"}]}", result);
    }
}
