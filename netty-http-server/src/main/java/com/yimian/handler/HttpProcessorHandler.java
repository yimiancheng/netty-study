package com.yimian.handler;

import com.yimian.SendHttpMsg;
import com.yimian.action.HttpRequestHandler;
import com.yimian.config.HandlerConfig;
import com.yimian.httpserver.Request;
import com.yimian.httpserver.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ThreadPoolExecutor;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HttpProcessorHandler
 * chengshaohua
 *
 * @date 2019/9/2 19:56
 */
public class HttpProcessorHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static final Logger LOG = LoggerFactory.getLogger(HttpProcessorHandler.class);

    private static final String FAVICON_ICO = "/favicon.ico";
    private ThreadPoolExecutor bizThreadPool;

    public HttpProcessorHandler(ThreadPoolExecutor bizThreadPool) {
        super(false);// 不自动释放，自己手动控制，否则丢到业务线程池后，body就直接被释放了
        this.bizThreadPool = bizThreadPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
        //默认同步处理
        boolean syncHandle = true;
        Channel channel = ctx.channel();
        boolean isKeepAlive = HttpUtil.isKeepAlive(httpRequest) && httpRequest.headers().contains(CONNECTION);
        LOG.info("接收到 httpRequest 类型 " + httpRequest.getClass().getName());

        try {
            if(!httpRequest.decoderResult().isSuccess()) {
                SendHttpMsg.sendHttpHtmlMsg("", false, isKeepAlive, channel);
            }

            //100 continue
            if(HttpUtil.is100ContinueExpected(httpRequest)) {
                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
                channel.writeAndFlush(response);
            }

            String uri = httpRequest.uri();

            if(FAVICON_ICO.equals(uri)) {
                SendHttpMsg.sendHttpHtmlMsg("", true, isKeepAlive, channel);
                return;
            }

            HttpRequestHandler handler = HandlerConfig.getHandler(uri);

            if(handler == null) {
                SendHttpMsg.sendHttpHtmlMsg(FORBIDDEN.reasonPhrase(), false, isKeepAlive, channel);
                return;
            }

            Request request = Request.buidRequest(httpRequest, ctx);
            Response response = Response.buidResponse(httpRequest, ctx);

            try {
                if(bizThreadPool != null) {
                    try {  // 丢到业务线程池执行
                        bizThreadPool.execute(new HttpHandleTask(request, response, handler));
                        syncHandle = false;
                    }
                    catch (Exception e) {  // 太忙了
                        LOG.error("Biz thread pool is exhausted, active:{}, max:{}, queue:{}", new Object[] {
                            bizThreadPool.getActiveCount(),
                            bizThreadPool.getMaximumPoolSize(),
                            bizThreadPool.getQueue().size()});
                        response.setStatus(TOO_MANY_REQUESTS);
                    }
                }
                else {
                    handler.invoke(request, response);
                }
            }
            catch(Exception ex) {
                LOG.error("处理异常 " + ex.getMessage(), ex);
                response.setStatus(INTERNAL_SERVER_ERROR);
                response.setContent(String.format("服务器异常【{}】", ex.getMessage()));
            }

            if(syncHandle && response != null && response.isSendResponse()) {
                SendHttpMsg.send(request, response);
            }
        }
        catch(Exception ex) {
            String msg = String.format("服务器异常【%s】", ex.getMessage());
            LOG.error(msg, ex);
            SendHttpMsg.sendHttpHtmlMsg(msg,false, isKeepAlive, channel);
        }
        finally {
            if(syncHandle) {
                ReferenceCountUtil.release(httpRequest);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("消息处理失败", cause.getCause());
    }
}
