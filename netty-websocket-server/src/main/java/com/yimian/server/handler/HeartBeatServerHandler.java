package com.yimian.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HeartBeatServerHandler
 *
 * @date 2019/8/31 21:01
 */
public class HeartBeatServerHandler extends IdleStateHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatServerHandler.class);

    public HeartBeatServerHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        LOG.info("接收到事件 " + (evt.getClass()) + " | " + evt.state().toString());

        if(evt.state().equals(IdleState.READER_IDLE)){
            ctx.channel().close();
            ctx.channel().writeAndFlush(new CloseWebSocketFrame());
        }
        else if(evt.state().equals(IdleState.WRITER_IDLE)){
            ctx.channel().writeAndFlush(new PingWebSocketFrame());
        }

        super.channelIdle(ctx, evt);
    }
}
/*public class HeartBeatServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        LOG.info("接收到消息类型" + (o.getClass().getName()));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOG.info("接收到事件" + JSON.toJSON(evt));
        super.userEventTriggered(ctx, evt);

    }
}*/
