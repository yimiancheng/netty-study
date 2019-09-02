package com.yimian.config;

/**
 * ServerConfig
 *
 * @date 2019/8/20 15:12
 */
public class ServerConfig {
    /**
     * 服务端启动端口
     */
    public static final Integer WEBSOCKET_PORT = 1601;

    /**
     * websocket请求url
     */
    public static final String WEB_SOCKET_PATH = "/websocket";

    /**
     * Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。
     */
    public static final Integer BACKLOG_SIZE = 1024;

    /**
     * boss线程池大小
     */
    public static final Integer BOOS_GROUP_THREAD_NUMBER = 4;

    /**
     * worker线程池大小
     */
    public static final Integer WORKER_GROUP_THREAD_NUMBER = 8;


}
