package com.yimian;

import com.yimian.handler.WebSocketClientFrameHandler;
import com.yimian.task.HeartBeatTimerTask;
import com.yimian.task.ReconnectTimerTask;
import com.yimian.thread.NamedThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SocketClient
 *
 * @date 2019/8/20 17:53
 */
public class SocketClient {
    private static final Logger LOG = LoggerFactory.getLogger(SocketClient.class);

    public static String CLIENT_VERSION = "client_version";
    private static final int DEFAULT_PORT = 80;
    /**
     * 长链重连间隔时间，单位s
     */
    public static long RECONNECT_INTERVAL = 10;
    /**
     * 长链心跳时间，单位s
     */
    public static long FETCH_PERIOD = 30;

    private String host;
    private int port;

    private Channel channel = null;
    private NioEventLoopGroup nioEventLoopGroup;

    public AtomicBoolean CHANNEL_IS_READY = new AtomicBoolean(false);

    private ScheduledExecutorService RECONNECT_TIMER;
    private ScheduledExecutorService HEARTBEAT_TIMER;

    static {
        // RECONNECT_TIMER = Executors.newSingleThreadScheduledExecutor();
    }

    private URI uri;


    public SocketClient(URI uri) {
        this.uri = uri;
    }

    private void start() {
        Bootstrap boot = new Bootstrap();
        nioEventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        try {
            HttpHeaders httpHeaders = new DefaultHttpHeaders();
            httpHeaders.add(CLIENT_VERSION, 1);

            WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(uri,
                WebSocketVersion.V13, null, false, httpHeaders);

            boot.group(nioEventLoopGroup)
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class);

            WebSocketClientFrameHandler webSocketFrameHandler = new WebSocketClientFrameHandler();
            webSocketFrameHandler.setSocketClient(this);

            ClientChannelInitializer clientChannelInitializer =
                new ClientChannelInitializer(webSocketClientHandshaker, webSocketFrameHandler);

            boot.handler(new LoggingHandler(LogLevel.INFO));
            boot.handler(clientChannelInitializer);

            port = (uri.getPort() == -1) ? DEFAULT_PORT : uri.getPort();
            host = uri.getHost();
            channel = boot.connect(host, port).sync().channel();
            LOG.info("SocketClient has started. CHANNEL_IS_READY = " + CHANNEL_IS_READY.get());
            webSocketFrameHandler.getChannelPromise().sync();
            LOG.info("SocketClient has started full. CHANNEL_IS_READY = " + CHANNEL_IS_READY.get());
        }
        catch(Exception ex) {
            ex.printStackTrace();
            LOG.error("connect error. uri " + uri.toString());
        }
        finally {
            // nioEventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 客户端连接服务端
     */
    public void connect() {
        stop();
        start();
        startReconnect();
        doHeartBeat();
    }

    /**
     * 开启线程-断开重连
     */
    public void startReconnect() {
        RECONNECT_TIMER = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("reconnect-schedule-pool", Boolean.TRUE, Thread.NORM_PRIORITY));

        // https://www.jianshu.com/p/502f9952c09b
        RECONNECT_TIMER.scheduleAtFixedRate(new ReconnectTimerTask(this),
            RECONNECT_INTERVAL * 1000L, RECONNECT_INTERVAL * 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * 心跳
     */
    private void doHeartBeat() {
        HEARTBEAT_TIMER = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("heartbeat-schedule-pool", Boolean.TRUE, Thread.NORM_PRIORITY));

        // https://www.jianshu.com/p/502f9952c09b
        HEARTBEAT_TIMER.scheduleAtFixedRate(new HeartBeatTimerTask(this),
            FETCH_PERIOD * 1000L, FETCH_PERIOD * 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * 客户端停止
     */
    public void stop() {
        try {
            if(nioEventLoopGroup != null) {
                nioEventLoopGroup.shutdownGracefully();
            }

            if(channel != null) {
                channel.close();
            }

            if(RECONNECT_TIMER != null) {
                RECONNECT_TIMER.shutdown();
                RECONNECT_TIMER = null;
            }

            if(HEARTBEAT_TIMER != null) {
                HEARTBEAT_TIMER.shutdown();
                HEARTBEAT_TIMER = null;
            }
        }
        catch(Exception ex) {
            //do nothing.
        }
    }


    public boolean isValid() {
        if (channel != null && channel.isActive()) {
            return true;
        } else {
            return false;
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
