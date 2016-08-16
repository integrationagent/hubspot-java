package com.integrationagent.hubspotApi.unit;

import com.integrationagent.hubspotApi.domain.HSCompany;
import com.integrationagent.hubspotApi.service.HSCompanyService;
import com.integrationagent.hubspotApi.service.HttpService;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HSCompanyJSONTest {

    HSCompanyService service = new HSCompanyService(new HttpService("key", "apiBase"));

    @Test
    public void parseCompanyData_Test() throws Exception {
        String inputData = "{\"portalId\": 62515,\"companyId\": 10444744,\"isDeleted\": false,\"properties\": {\"description\": \"text\"}}";
        JSONObject jsonObject = new JSONObject(inputData);
        HSCompany company = service.parseCompanyData(jsonObject);
        assertEquals(company.getId(), 10444744);
        assertEquals(company.getProperty("description"),"text");
    }

    @Test
    public void toJson_Test() throws Exception {
        String inputData = "{\"portalId\": 62515,\"companyId\": 10444744,\"isDeleted\": false,\"properties\": {\"description\": \"text\"}}";
        JSONObject jsonObject = new JSONObject(inputData);
        HSCompany company = service.parseCompanyData(jsonObject);

        String result = company.toJson().toString();
        assertEquals("{\"properties\":[{\"name\":\"description\",\"value\":\"text\"}]}", result);
    }
}
