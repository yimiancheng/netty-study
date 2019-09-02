package com.yimian.server;

import com.yimian.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;

/**
 * SocketServer
 *
 * @date 2019/8/20 14:50
 */
public class SocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(SocketServer.class);

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;

    /**
     * websocket server start.
     *
     * https://blog.csdn.net/zuixiaoyao_001/article/details/90198968
     */
    public static final void start() {
         bossGroup = new NioEventLoopGroup(ServerConfig.BOOS_GROUP_THREAD_NUMBER);
         workerGroup = new NioEventLoopGroup(ServerConfig.WORKER_GROUP_THREAD_NUMBER);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);

            serverBootstrap
                // 标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                .option(ChannelOption.SO_BACKLOG, ServerConfig.BACKLOG_SIZE)
                // 地址复用，默认值False
                .option(ChannelOption.SO_REUSEADDR, true)
                // ByteBuf的分配器
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                /**
                 * 禁止使用Nagle算法，使用于小数据即时传输
                 * 该值设置Nagle算法的启用，该算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，
                 * 如果需要发送一些较小的报文，则需要禁用该算法。
                 * Netty默认禁用该算法，从而最小化报文传输延时。
                 */
                .option(ChannelOption.TCP_NODELAY, true);


            serverBootstrap
                .channel(NioServerSocketChannel.class);


            serverBootstrap
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                // TCP数据发送缓冲区大小
                .childOption(ChannelOption.SO_SNDBUF,1024*1024);

            ServerChannelInitializer serverChannelInitializer = new ServerChannelInitializer();
            serverBootstrap.childHandler(serverChannelInitializer);

            ChannelFuture future = serverBootstrap.bind(ServerConfig.WEBSOCKET_PORT);

            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        LOG.info("Server have success bind to {}", ServerConfig.WEBSOCKET_PORT);

                    } else {
                        LOG.error("Server fail bind to {}", ServerConfig.WEBSOCKET_PORT);
                        LOG.error("Server start fail !", future.cause());
                        stop();
                    }

                }
            });

            LOG.info("WebSocketServer has started, waiting for client to connect");
            Channel channel = future.sync().channel();
            channel.closeFuture().sync();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.exit(0);
    }
}
