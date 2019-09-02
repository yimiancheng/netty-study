package com.yimian.task;

import com.yimian.websocket.SendMsg;
import com.yimian.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.TimerTask;

/**
 * HeartBeatTimerTask
 *
 * @date 2019/9/2 16:24
 */
public class HeartBeatTimerTask extends TimerTask {
    private static final Logger LOG = LoggerFactory.getLogger(ReconnectTimerTask.class);
    private SocketClient socketClient;

    public HeartBeatTimerTask(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public void run() {
        if(socketClient != null && socketClient.isValid()) {
            SendMsg.send(socketClient.getChannel(), "客户端心跳消息 => " + System.currentTimeMillis());
        }
    }
}
