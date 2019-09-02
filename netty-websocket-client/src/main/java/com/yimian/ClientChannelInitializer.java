package com.yimian;

import com.yimian.handler.HeartBeatClientHandler;
import com.yimian.handler.WebSocketClientFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * ClientChannelInitializer
 *
 * @date 2019/8/31 16:05
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private WebSocketClientHandshaker webSocketClientHandshaker;
    private WebSocketClientFrameHandler webSocketFrameHandler;

    public ClientChannelInitializer(WebSocketClientHandshaker webSocketClientHandshaker, WebSocketClientFrameHandler
        webSocketFrameHandler) {
        this.webSocketClientHandshaker = webSocketClientHandshaker;
        this.webSocketFrameHandler = webSocketFrameHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new HttpClientCodec());//Http协议编码解码器
        pipeline.addLast(new HttpObjectAggregator(65536));//聚合 HttpRequest
        pipeline.addLast(new IdleStateHandler(5, 10, 0));
        //会处理ping pong close消息
        pipeline.addLast(new WebSocketClientProtocolHandler(webSocketClientHandshaker,true));
        pipeline.addLast(webSocketFrameHandler);
    }
}
