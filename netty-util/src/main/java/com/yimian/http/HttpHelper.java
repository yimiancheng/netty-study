package com.yimian.http;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpHelper {
    public final static Map<String, String> parseParameters(String query) {
        if(query == null) {
            return new HashMap<String, String>();
        }
        String[] keyValues = query.split("&");
        if(keyValues == null) {
            return new HashMap<String, String>();
        }
        else {
            Map<String, String> result = new HashMap<String, String>();
            for(String keyValue : keyValues) {
                int pos = keyValue.indexOf("=");
                if(pos == -1) {
                    result.put(keyValue, "");
                }
                else {
                    String key = keyValue.substring(0, pos);
                    String value = keyValue.substring(pos + 1);
                    result.put(key, URLDecoder.decode(value));
                }
            }
            return result;
        }
    }
}
