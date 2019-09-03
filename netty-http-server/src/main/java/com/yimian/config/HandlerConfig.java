package com.yimian.config;

import com.yimian.action.DefaultHttpRequestHandler;
import com.yimian.action.HttpRequestHandler;
import org.apache.commons.lang3.StringUtils;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HandlerConfig
 * chengshaohua
 *
 * @date 2019/9/3 15:20
 */
public class HandlerConfig {
    public static ConcurrentHashMap<String, HttpRequestHandler> HANDLER_MAP = new ConcurrentHashMap<String, HttpRequestHandler>();
    public static final HttpRequestHandler DEFAULT_HANDLER = new DefaultHttpRequestHandler();

    //url分隔符
    private final static char separator = '/';

    static {
        //二级地址
        // HANDLER_MAP.put("/v1/pullMsg", null);
    }

    public static HttpRequestHandler getHandler(String path) {
        String key = getURIKey(path, 2);
        HttpRequestHandler httpRequestHandler = HANDLER_MAP.get(key);
        return httpRequestHandler == null ? DEFAULT_HANDLER : httpRequestHandler;
    }

    public static String getURIKey(String uri, int level) {
        if(StringUtils.isBlank(uri)) {
            return "";
        }

        int io = uri.indexOf("?");

        if(io > 0) {
            uri = uri.substring(0, io);
        }

        int start = uri.charAt(0) == separator ? 1 : 0;
        int cnt = 0;
        StringBuilder sb = new StringBuilder(uri.length()).append(separator);
        // 开始遍历
        for(int i = start; i < uri.length(); i++) {
            char c = uri.charAt(i);
            if(c != separator) {
                sb.append(c);
            }
            else {
                if(++cnt == level) {
                    break;
                }
                else {
                    sb.append(separator);
                }
            }
        }

        if(sb.length() > 1 && sb.charAt(sb.length() - 1) == separator) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
