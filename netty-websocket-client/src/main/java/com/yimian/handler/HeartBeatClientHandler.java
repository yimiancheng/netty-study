package com.yimian.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HeartBeatClientHandler
 *
 * @date 2019/8/31 21:57
 */
public class HeartBeatClientHandler extends IdleStateHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatClientHandler.class);

    public HeartBeatClientHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        LOG.info("接收到事件 " + (evt.getClass()) + " | " + evt.state().toString());

        if(evt.state().equals(IdleState.WRITER_IDLE)){
            /*ctx.channel().close();*/
            // SendMsg.send(ctx.channel(), "客户端 心跳事件 => " + evt.state().toString());
            ctx.channel().writeAndFlush(new PingWebSocketFrame());
        }

        super.channelIdle(ctx, evt);
    }
}
