package com.yimian.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;

/**
 * Response
 * chengshaohua
 *
 * @date 2019/9/3 14:36
 */
public class Response {
    private static final Logger LOG = LoggerFactory.getLogger(Response.class);
    //netty request
    private HttpRequest nettyHttpRequest;
    private ChannelHandlerContext ctx;
    private Channel channel;

    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = CONTENT_TYPE_HTML;
    private String charset = CharsetUtil.UTF_8.name();
    //返回内容
    private ByteBuf content = Unpooled.EMPTY_BUFFER;
    private boolean sendResponse = true;

    private Response(HttpRequest nettyHttpRequest, ChannelHandlerContext ctx) {
        this.nettyHttpRequest = nettyHttpRequest;
        this.ctx = ctx;
        this.channel = ctx.channel();
    }

    public HttpRequest getNettyHttpRequest() {
        return nettyHttpRequest;
    }

    public void setNettyHttpRequest(HttpRequest nettyHttpRequest) {
        this.nettyHttpRequest = nettyHttpRequest;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isSendResponse() {
        return sendResponse;
    }

    public void setSendResponse(boolean sendResponse) {
        this.sendResponse = sendResponse;
    }

    public static Response buidResponse(HttpRequest nettyHttpRequest, ChannelHandlerContext ctx) {
        return new Response(nettyHttpRequest, ctx);
    }

    public ByteBuf getContent() {
        return content;
    }

    public void setContent(String contentText) {
        this.content = Unpooled.copiedBuffer(contentText, Charset.forName(charset));
        this.content = content;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    //普通文本
    public final static String CONTENT_TYPE_TEXT = "text/plain";
    public final static String CONTENT_TYPE_HTML = "text/html";
    public final static String CONTENT_TYPE_XML = "text/xml";
    public final static String CONTENT_TYPE_JAVASCRIPT = "application/javascript";
    public final static String CONTENT_TYPE_JSON = "application/json";
}
