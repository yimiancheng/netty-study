package com.yimian;

import com.yimian.config.ServerConfig;
import com.yimian.thread.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * HttpServer
 * chengshaohua
 * @date 2019/9/2 18:00
 */
public class HttpServer {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private Integer port;
    private boolean useSSL;
    /**
     * 业务线程池
     */
    private Integer corePoolSize = 100;
    private Integer maximumPoolSize = 100;
    private Integer queueSize = 0;
    private ThreadPoolExecutor bizThreadPool;

    public HttpServer() {
        this(null, false);
    }

    public HttpServer(Integer port, boolean useSSL) {
        this.port = port == null ? ServerConfig.HTTP_SERVER_PORT : 80;
        this.useSSL = useSSL;
    }

    /**
     * 启动服务端
     */
    public void start() {
        shutdown();

        bizThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60000, TimeUnit.MILLISECONDS,
            queueSize <= 0 ? new SynchronousQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queueSize),
            new NamedThreadFactory("HTTP-BIZ"));

        bossGroup = new NioEventLoopGroup(ServerConfig.BOOS_GROUP_THREAD_NUMBER,
            new NamedThreadFactory("netty-http-server-boss"));

        workerGroup = new NioEventLoopGroup(ServerConfig.WORKER_GROUP_THREAD_NUMBER,
            new NamedThreadFactory("netty-http-server-worker"));
        final SslContext sslCtx;

        if(useSSL) {
            //加密暂时不考虑
        }
        else {
            sslCtx = null;
        }

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);

            serverBootstrap
                // 标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                .option(ChannelOption.SO_BACKLOG, ServerConfig.BACKLOG_SIZE);

            serverBootstrap.channel(NioServerSocketChannel.class);

            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true)
                           .childOption(ChannelOption.TCP_NODELAY, true);

            HttpServerChannelInitializer channelInitializer = new HttpServerChannelInitializer(bizThreadPool);
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            serverBootstrap.childHandler(channelInitializer);

            ChannelFuture future = serverBootstrap.bind(port);

            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        LOG.info("Server have success bind to {}", port);

                    } else {
                        LOG.error("Server fail bind to {}", port);
                        LOG.error("Server start fail !", future.cause());
                        shutdown();
                    }

                }
            });

            LOG.info("WebSocketServer has started, waiting for client to connect");
            Channel channel = future.sync().channel();
            channel.closeFuture().sync();
        }
        catch(Exception ex) {
            LOG.error("Server fail bind to {}", port, ex);
        }
        finally {
            shutdown();
        }
    }

    public void shutdown() {
        if(bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if(workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
