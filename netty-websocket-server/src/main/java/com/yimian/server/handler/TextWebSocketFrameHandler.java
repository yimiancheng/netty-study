package com.yimian.server.handler;

import com.yimian.server.SocketServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;

/**
 * TextWebSocketFrameHandler
 *
 * @date 2019/8/20 16:24
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger LOG = LoggerFactory.getLogger(SocketServer.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     *
     * @param channelHandlerContext
     * @param textWebSocketFrame
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame)
        throws Exception
    {
        if(textWebSocketFrame instanceof TextWebSocketFrame) {
            String message = textWebSocketFrame.content().toString(Charset.forName("utf-8"));

            // String message = textWebSocketFrame.text();
            LOG.info("服务端TextWebSocketFrame 接收到消息：" + message);
        }
        else {
            LOG.info("接收到消息类型" + (textWebSocketFrame.getClass().getName()));
        }
    }




}
