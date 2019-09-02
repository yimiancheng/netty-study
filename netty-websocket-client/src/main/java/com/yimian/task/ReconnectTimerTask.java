package com.yimian.task;

import com.yimian.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.TimerTask;

/**
 * ReconnectTimerTask
 *
 * @date 2019/9/2 16:03
 */
public class ReconnectTimerTask extends TimerTask {
    private static final Logger LOG = LoggerFactory.getLogger(ReconnectTimerTask.class);
    private SocketClient socketClient;

    public ReconnectTimerTask(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public void run() {
        if(socketClient != null && !socketClient.isValid()) {
            LOG.info("=== 客户端重连 " + System.currentTimeMillis());
            socketClient.connect();
        }
    }
}
