package com.yimian.action;

import com.yimian.handler.HttpProcessorHandler;
import com.yimian.httpserver.Request;
import com.yimian.httpserver.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpRequestHandler
 * chengshaohua
 *
 * @date 2019/9/3 14:53
 */
public abstract class HttpRequestHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);

    /**
     * 实现处理请求的方法
     * @param request
     * @param response
     */
    public abstract void invoke(Request request, Response response);
}
