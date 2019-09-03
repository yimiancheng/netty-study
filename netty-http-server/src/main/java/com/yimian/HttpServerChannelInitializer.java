package com.yimian;

import com.yimian.handler.HttpProcessorHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * HttpServerChannelInitializer
 * chengshaohua
 *
 * @date 2019/9/2 18:20
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ThreadPoolExecutor bizThreadPool;

    public HttpServerChannelInitializer(ThreadPoolExecutor bizThreadPool) {
        this.bizThreadPool = bizThreadPool;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        //必须放在第一位
        /*pipeline.addLast(new HttpResponseEncoder());
        pipeline.addLast(new HttpRequestDecoder());*/
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        //大文件传输处理
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new ChunkedWriteHandler());
        //gzip压缩
        pipeline.addLast(new HttpContentCompressor());
        //跨域
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
        pipeline.addLast(new CorsHandler(corsConfig));
        pipeline.addLast(new HttpProcessorHandler(bizThreadPool));
    }
}
