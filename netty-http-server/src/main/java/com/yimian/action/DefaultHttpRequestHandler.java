package com.yimian.action;

import com.alibaba.fastjson.JSON;
import com.yimian.annotation.PATH;
import com.yimian.httpserver.Request;
import com.yimian.httpserver.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultHttpRequestHandler
 * chengshaohua
 *
 * @date 2019/9/3 15:15
 */
@PATH("/")
public class DefaultHttpRequestHandler extends HttpRequestHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHttpRequestHandler.class);

    @Override
    public void invoke(Request request, Response response) {
        response.setContentType(Response.CONTENT_TYPE_JSON);
        response.setContent(JSON.toJSONString(request));
    }
}
