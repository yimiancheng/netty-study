package com.yimian.config;

import java.nio.charset.Charset;

/**
 * ServerConfig
 * chengshaohua
 *
 * @date 2019/9/2 18:02
 */
public class ServerConfig {
    /**
     * 服务端启动端口
     */
    public static final Integer HTTP_SERVER_PORT = 80;

    /**
     * boss线程池大小
     */
    public static final Integer BOOS_GROUP_THREAD_NUMBER = 4;

    /**
     * worker线程池大小
     */
    public static final Integer WORKER_GROUP_THREAD_NUMBER = 8;

    /**
     * Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。
     */
    public static final Integer BACKLOG_SIZE = 1024;

    /**
     * 默认字符集 utf-8
     */
    public final static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
}
