import com.yimian.websocket.SendMsg;
import com.yimian.server.SocketServer;

/**
 * ServerTest
 *
 * @date 2019/8/20 17:13
 */
public class ServerTest {
    public static void main(String[] args) {
       SendMsg.startSendMsg();
       SocketServer.start();
    }
}
