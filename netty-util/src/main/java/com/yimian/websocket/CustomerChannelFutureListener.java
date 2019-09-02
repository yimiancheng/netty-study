package com.yimian.websocket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CustomerChannelFutureListener implements ChannelFutureListener {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerChannelFutureListener.class);

        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            // LOG.info(JSON.toJSONString(channelFuture));
            if(channelFuture.isDone() && channelFuture.isSuccess()){
               // LOG.info("send success.");
            }
            else {
                channelFuture.channel().close();
                LOG.info("send error. cause = " + channelFuture.cause());
                channelFuture.cause().printStackTrace();
            }
        }
    }