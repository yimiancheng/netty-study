package com.yimian.server;

import com.yimian.config.ServerConfig;
import com.yimian.server.handler.CustomUrlHandler;
import com.yimian.server.handler.HeartBeatServerHandler;
import com.yimian.server.handler.WebSocketServerFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

/**
 * ServerChannelInitializer
 *
 * @date 2019/8/20 16:15
 */
public final class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // Http协议编码解码器,不能完全解析post请求，只能从uri中获取参数
        //pipeline.addLast(new LoggingHandler(LogLevel.ERROR));
        pipeline.addLast(new HttpServerCodec());
        //HttpRequest
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        pipeline.addLast(new ChunkedWriteHandler());//用于大数据的分区传输
        pipeline.addLast(new HeartBeatServerHandler(15, 5, 0));
        pipeline.addLast(new CustomUrlHandler());
        //处理握手协议
        pipeline.addLast(new WebSocketServerProtocolHandler(ServerConfig.WEB_SOCKET_PATH, null, true));
        pipeline.addLast(new WebSocketServerFrameHandler());
        // pipeline.addLast(textWebSocketFrameHandler);
    }
}
