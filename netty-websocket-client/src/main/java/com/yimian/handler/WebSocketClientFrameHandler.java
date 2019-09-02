package com.yimian.handler;

import com.yimian.websocket.SendMsg;
import com.yimian.SocketClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocketClientFrameHandler
 *
 * @date 2019/8/20 16:42
 */
public class WebSocketClientFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketClientFrameHandler.class);
    private SocketClient socketClient;
    private ChannelPromise channelPromise;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOG.info("客户端接收到事件 " + (evt.getClass()) + " | " + evt.toString());

        if(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE.equals(evt)) {
            LOG.info(ctx.channel().id().asShortText() + " 握手完成！");
            socketClient.CHANNEL_IS_READY.set(true);
            channelPromise.setSuccess();
            // SendMsg.send(ctx.channel(),"客户端握手完成消息 -》服务器时间: " + System.currentTimeMillis());
        }
        else if(evt instanceof IdleStateEvent){
            //ctx.channel().writeAndFlush(new PingWebSocketFrame());
            IdleStateEvent evtIdle = (IdleStateEvent) evt;
            switch(evtIdle.state()) {
                case WRITER_IDLE:
                    // SendMsg.send(ctx.channel(),"客户端 ping 消息 -》服务器时间: " + System.currentTimeMillis());
                    ctx.channel().writeAndFlush(new PingWebSocketFrame());
                case READER_IDLE:
                    // SendMsg.send(ctx.channel(),"客户端 ping 消息 -》服务器时间: " + System.currentTimeMillis());
                    ctx.channel().writeAndFlush(new PingWebSocketFrame());
                default:
                    break;
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOG.info("打开连接 handlerAdded SUCCESS. | name = " +channel.id().asShortText());
        super.handlerAdded(ctx);
        channelPromise = ctx.newPromise();
    }

    /**
     * Channel 已经被注册到了EventLoop
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOG.info("注册成功 channelRegistered SUCCESS. | name = " +channel.id().asShortText());
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
        LOG.info("活动状态 channelActive SUCCESS. | name = " +channel.id().asShortText());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) throws
        Exception {
        if(webSocketFrame instanceof TextWebSocketFrame) {
            // String message = textWebSocketFrame.content().toString(Charset.forName("utf-8"));
            String message = ((TextWebSocketFrame) webSocketFrame).text();
            LOG.info("客户端接收到消息：" + message);
        }
        else {
            LOG.info("接收到消息类型" + (webSocketFrame.getClass().getName()));
        }

        SendMsg.sendPong(channelHandlerContext.channel());
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
        LOG.info("连接断开 channelUnregistered SUCCESS. | name = " +channel.id().asShortText());
        super.channelUnregistered(ctx);
        channelPromise = null;
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    public void setSocketClient(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    public ChannelPromise getChannelPromise() {
        return channelPromise;
    }
}
