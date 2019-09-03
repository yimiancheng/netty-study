package com.yimian.server.handler;

import com.alibaba.fastjson.JSON;
import com.yimian.http.HttpHelper;
import com.yimian.http.Parts;
import com.yimian.server.SocketServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * CustomUrlHandler
 *
 * @date 2019/9/2 16:42
 */
public class CustomUrlHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger LOG = LoggerFactory.getLogger(SocketServer.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws
        Exception
    {
        Channel channel = channelHandlerContext.channel();
        String uri = fullHttpRequest.uri();
        LOG.info("请求连接的uri {}", uri);
        int idx = uri.indexOf("?");

        if(idx > 0) {
            Parts parts = new Parts(uri);
            LOG.info("------ 请求连接的parts {}", JSON.toJSON(parts));
            Map<String, String> parameters = HttpHelper.parseParameters(parts.getQuery());
            LOG.info("------ 请求连接的parameters {}", JSON.toJSON(parameters));

            if(parameters.containsKey("test")) {
                LOG.info("------ 请求连接的uri非法");
                close(channel);
            }
            else {
                fullHttpRequest.setUri(uri.substring(0, idx));

                //下一个handler处理
                fullHttpRequest.retain();
                channelHandlerContext.fireChannelRead(fullHttpRequest);
            }
        }
        else {
            close(channel);
        }
    }

    private void close( Channel channel) {
        channel.close();
    }
}
