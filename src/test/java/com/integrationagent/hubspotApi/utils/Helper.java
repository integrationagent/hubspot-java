package com.integrationagent.hubspotApi.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Helper {

    public static String getProperty(String key) {
        Properties p = new Properties();
        try {
            p.load(new FileReader(new File("src//test//resources//config.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String val = p.getProperty(key);
        return val;
    }
}
