import com.alibaba.fastjson.JSON;
import com.yimian.SocketClient;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;

/**
 * ClientTest
 *
 * @date 2019/8/20 18:11
 */
public class ClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(ClientTest.class);
    public static String OCEAN_URL = "ws://localhost:1601/websocket";

    public static URI URI_WEBSOCKET = null;

    static {
        // OCEAN_URL = "ws://message.1688.com/websocket";

        if(URI_WEBSOCKET == null) {
            synchronized(ClientTest.class) {
                if(URI_WEBSOCKET == null) {
                    try {
                        URI_WEBSOCKET = new URI(OCEAN_URL);
                    }
                    catch(Exception ex) {
                        // do nothing.
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        SocketClient socketClient = new SocketClient(URI_WEBSOCKET);
        socketClient.connect();

        // socketClient.send("客户端消息 -》服务器时间: " + System.currentTimeMillis());
    }
}
