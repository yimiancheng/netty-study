package com.yimian.server.handler;

import com.alibaba.fastjson.JSON;
import com.yimian.websocket.SendMsg;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocketServerFrameHandler
 *
 * @date 2019/8/20 16:42
 */
public class WebSocketServerFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServerFrameHandler.class);
    private AttributeKey<String> TOKEN_URL = AttributeKey.valueOf("token");

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOG.info("打开连接 handlerAdded SUCCESS. | name = " +channel.id().asShortText() +
            " | ip = " + channel.remoteAddress());
        super.handlerAdded(ctx);
    }

    /**
     * Channel 已经被注册到了EventLoop
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOG.info("注册成功 channelRegistered SUCCESS. | name = " + ctx.name() +
            " | ip = " + channel.remoteAddress() + " | " + JSON.toJSONString(channel));
        super.channelRegistered(ctx);
    }

    /**
     * Channel 处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOG.info("活动状态 channelActive SUCCESS. | name = " +channel.id().asShortText() +
            " | ip = " + channel.remoteAddress());
        super.channelActive(ctx);
        SendMsg.put(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) throws
        Exception
    {
        Channel channel = channelHandlerContext.channel();
        // Attribute<String> token = channel.attr(TOKEN_URL);
        // channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("服务端消息 =》服务器时间: " + System.currentTimeMillis()));
        if(webSocketFrame instanceof TextWebSocketFrame) {
            // String message = textWebSocketFrame.content().toString(Charset.forName("utf-8"));
            String message = ((TextWebSocketFrame) webSocketFrame).text();
            LOG.info("服务端接收到消息：" + message);
        }
        else {
            LOG.info("接收到消息类型" + (webSocketFrame.getClass().getName()));
        }

        SendMsg.sendPing(channelHandlerContext.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("消息处理失败: " + cause.getMessage(), cause);
        ctx.close();
    }

    /**
     * Channel 已经被创建，但还未注册到EventLoop
     * 连接断开
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOG.info("连接断开 channelUnregistered SUCCESS. | name = " +channel.id().asShortText() +
            " | ip = " + channel.remoteAddress());
        super.channelUnregistered(ctx);
        SendMsg.remove(channel);
    }
}
