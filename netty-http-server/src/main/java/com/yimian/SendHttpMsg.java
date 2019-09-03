package com.yimian;

import com.yimian.config.ServerConfig;
import com.yimian.httpserver.Request;
import com.yimian.httpserver.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SendHttpMsg
 * chengshaohua
 *
 * @date 2019/9/2 21:22
 */
public class SendHttpMsg {
    private static final Logger LOG = LoggerFactory.getLogger(SendHttpMsg.class);

    private static void sendHttpMsg(ByteBuf content, HttpResponseStatus status, boolean isKeepAlive, Channel channel,
        String contentTypeValue)
    {
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentTypeValue);
        HttpUtil.setContentLength(response, content.readableBytes());

        try {
            ChannelFuture channelFuture = channel.writeAndFlush(response);
            if(isKeepAlive) {
                HttpUtil.setKeepAlive(response, true);
                channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
            else {
                HttpUtil.setKeepAlive(response, false);//set keepalive closed
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
        catch(Exception ex) {
            LOG.error("Failed to send HTTP response to remote, cause by:", ex);
        }
    }

    public static void sendHttpHtmlMsg(String resultStr, boolean isSuccess, boolean isKeepAlive, Channel channel) {
        ByteBuf content = Unpooled.copiedBuffer(resultStr, ServerConfig.DEFAULT_CHARSET);
        HttpResponseStatus status = isSuccess ? HttpResponseStatus.OK : HttpResponseStatus.INTERNAL_SERVER_ERROR;
        sendHttpMsg(content, status, isKeepAlive, channel, "text/html; charset=UTF-8");
    }

    public static void send(Request request, Response response) {
        String contentTypeValue = new StringBuilder(response.getContentType())
            .append("; charset=").append(response.getCharset()).toString();
        sendHttpMsg(response.getContent(), response.getStatus(),
            request.isKeepAlive(), response.getChannel(), contentTypeValue);
    }
}
