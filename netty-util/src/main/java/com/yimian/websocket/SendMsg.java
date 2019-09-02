package com.yimian.websocket;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SendMsg
 *
 * @date 2019/8/30 18:02
 */
public class SendMsg {
    private static final Logger LOG = LoggerFactory.getLogger(SendMsg.class);
    public static ConcurrentHashMap<String, Channel> ALL_CHANNEL = new ConcurrentHashMap<String, Channel>();

    public static void startSendMsg() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    sendMsgTest();
                }
            }
        });

        thread.start();
    }

    public static void sendMsgTest() {
        try {
            Map<String, Channel> map = Collections.unmodifiableMap(ALL_CHANNEL);
            LOG.info("map size = " + map.size());
            if(MapUtils.isEmpty(map)) {
                Thread.sleep(2000);
                return;
            }

            for(Map.Entry<String, Channel> entry : map.entrySet()) {
                LOG.info("------------- key = " + entry.getKey());
                send(entry.getValue(), "服务端发送消息 " + entry.getKey() + " | " + System.currentTimeMillis());
            }

            Thread.sleep(10000);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void put(Channel channel) {
        ALL_CHANNEL.put(channel.id().asShortText(), channel);
    }

    public static void remove(Channel channel) {
        ALL_CHANNEL.remove(channel.id().asShortText());
    }

    public static void send(Channel channel, Object msg) {
        final String textMsg = JSON.toJSONString(msg);

        if(channel != null && channel.isActive()) {
            TextWebSocketFrame frame = new TextWebSocketFrame(textMsg);
            channel.writeAndFlush(frame)
                .addListener(new CustomerChannelFutureListener());
        }
        else {
            LOG.error("消息发送失败！ textMsg = " + textMsg);
        }
    }

    public static void sendPing(Channel channel) {
        if(channel != null && channel.isActive()) {
            channel.writeAndFlush(new PingWebSocketFrame())
                .addListener(new CustomerChannelFutureListener());
        }
        else {
            LOG.error("消息发送失败！ ping");
        }
    }

    public static void sendPong(Channel channel) {
        if(channel != null && channel.isActive()) {
            channel.writeAndFlush(new PongWebSocketFrame())
                .addListener(new CustomerChannelFutureListener());
        }
        else {
            LOG.error("消息发送失败！ pong");
        }
    }

}

