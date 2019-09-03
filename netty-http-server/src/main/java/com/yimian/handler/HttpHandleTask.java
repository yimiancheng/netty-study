package com.yimian.handler;

import com.yimian.SendHttpMsg;
import com.yimian.action.HttpRequestHandler;
import com.yimian.filter.Filter;
import com.yimian.httpserver.Request;
import com.yimian.httpserver.Response;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

public class HttpHandleTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(HttpHandleTask.class);
    private HttpRequestHandler handler;
    private Request request;
    private Response response;
    private Filter filter; //暂时不考虑

    public HttpHandleTask(Request request, Response response, HttpRequestHandler handler) {
        this.request = request;
        this.response = response;
        this.handler = handler;
    }

    public void run() {
        try {
            Channel channel = response.getChannel();

            if(channel.isActive()) { // 已断开连接，丢弃
                handler.invoke(request, response);
            }
            else {
                channel.close();
            }
        }
        catch (Exception ex){
            String msg = String.format("服务器线程处理异常【%s】", ex.getMessage());
            LOG.error(msg, ex);
            response.setStatus(INTERNAL_SERVER_ERROR);
            response.setContent(msg);
        }
        finally {
            if(response != null && response.isSendResponse()) {
                SendHttpMsg.send(request, response);
            }
            ReferenceCountUtil.release(request.getCtx());
        }
    }
}