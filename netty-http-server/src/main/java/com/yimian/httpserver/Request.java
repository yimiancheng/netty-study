package com.yimian.httpserver;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.yimian.handler.HttpProcessorHandler;
import com.yimian.http.HttpParamOutputStream;
import com.yimian.http.Parts;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;

/**
 * Request
 * chengshaohua
 *
 * @date 2019/9/3 11:24
 */
public class Request {
    private static final Logger LOG = LoggerFactory.getLogger(Request.class);
    //netty request
    @JSONField(serialize = false)
    private HttpRequest nettyHttpRequest;
    @JSONField(serialize = false)
    private ChannelHandlerContext ctx;

    //uri
    private Parts parts;
    //ip
    private String ip;
    //方法
    private HttpMethod method;
    //keep alive(Connection != close && keep-alive && 1.1) HttpVersion.HTTP_1_0.text()
    private boolean isKeepAlive;
    private String httpVersion;

    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, Object> params = new HashMap<String, Object>();
    private Map<String, Cookie> cookies = new HashMap<String, Cookie>();

    private Request(HttpRequest nettyHttpRequest, ChannelHandlerContext ctx) {
        this.nettyHttpRequest = nettyHttpRequest;
        this.ctx = ctx;

        String uri = nettyHttpRequest.uri();
        this.parts = new Parts(uri);
        this.method = nettyHttpRequest.method();
        this.httpVersion = nettyHttpRequest.protocolVersion().text();
        this.isKeepAlive = HttpUtil.isKeepAlive(nettyHttpRequest) && nettyHttpRequest.headers().contains(CONNECTION);
        putHeadersAndCookies(nettyHttpRequest);
        putIp(ctx);

        putUriParam(uri);

        if(nettyHttpRequest.method() != HttpMethod.GET) {
            // getContentStr();//测试代码
            putContentParam(this.nettyHttpRequest);
        }

    }

    private void getContentStr() {
        if(nettyHttpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullMsg = (FullHttpRequest) nettyHttpRequest;
            ByteBuf content = fullMsg.content();
            int size = content.readableBytes();

            if(size > 0) {
                byte[] bytes = new byte[size];
                content.readBytes(bytes);

                try {
                    String body = new String(bytes, CharsetUtil.UTF_8.name());
                    LOG.info("body: " + body);
                }
                catch(Exception ex) {
                    LOG.info("error-body: " + new String(bytes), ex);
                }

            }
        }
    }

    /**
     * 解析uri参数
     */
    private void putUriParam(String uri) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);

        if(null != queryStringDecoder) {
            List<String> valueList;
            for(Map.Entry<String, List<String>> entry : queryStringDecoder.parameters().entrySet()) {
                valueList = entry.getValue();
                if(null != valueList) {
                    params.put(entry.getKey(), 1 == valueList.size() ? valueList.get(0) : valueList);
                }
            }
        }
    }

    private void putContentParam(HttpRequest nettyHttpRequest) {
        HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(nettyHttpRequest);

        try {
            LOG.info("参数个数 size = {}",postRequestDecoder.getBodyHttpDatas().size());

            for(InterfaceHttpData data : postRequestDecoder.getBodyHttpDatas()) {
                InterfaceHttpData.HttpDataType dataType = data.getHttpDataType();

                switch(dataType) {
                    case Attribute:
                        //普通参数
                        Attribute attribute = (Attribute) data;

                        try {
                            params.put(attribute.getName(), attribute.getValue());
                        }
                        catch(Exception ex) {
                            LOG.error(attribute.getName() + " 参数解析失败！ {}", JSON.toJSON(attribute), ex);
                        }
                        break;
                    case FileUpload:
                        FileUpload fileUpload = (FileUpload) data;

                        if(fileUpload.isCompleted()) {
                            try {
                                params.put(data.getName(), fileUpload.getFile());
                            }
                            catch(Exception ex) {
                                LOG.error("Get file param [{}] error!", data.getName(), ex);
                            }
                        }
                    default:
                        break;
                }
            }
        }
        finally {
            //postRequestDecoder.destroy();
        }

    }

    /**
     * 获取IP
     */
    private void putIp(ChannelHandlerContext ctx) {
        String remoteIp = headers.get("X-Forwarded-For");

        if(ip == null) {
            remoteIp = headers.get("X-Real-IP");
        }

        if (remoteIp != null) { // 可能是vip nginx等转发后的ip
            this.ip = NetUtil.getMultistageReverseProxyIp(remoteIp);
        }
        else {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            this.ip = inetSocketAddress.getAddress().getHostAddress();
        }
    }


    /**
     * 获取请求header
     */
    private void putHeadersAndCookies(HttpRequest nettyHttpRequest) {
        HttpHeaders httpHeaders = nettyHttpRequest.headers();
        final String cookieString = HttpHeaderNames.COOKIE.toString();

        for(Map.Entry<String, String> entry : httpHeaders) {
            if(StringUtils.equalsIgnoreCase(cookieString, entry.getKey())) {
                final Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(entry.getValue());

                for(Cookie cookie : cookies) {
                    this.cookies.put(cookie.name(), cookie);
                }

                continue;
            }

            this.headers.put(entry.getKey(), entry.getValue());
        }
    }

    public HttpRequest getNettyHttpRequest() {
        return nettyHttpRequest;
    }

    public void setNettyHttpRequest(HttpRequest nettyHttpRequest) {
        this.nettyHttpRequest = nettyHttpRequest;
    }

    public Parts getParts() {
        return parts;
    }

    public void setParts(Parts parts) {
        this.parts = parts;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public static Request buidRequest(HttpRequest nettyHttpRequest, ChannelHandlerContext ctx) {
        return new Request(nettyHttpRequest, ctx);
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        isKeepAlive = keepAlive;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public boolean isIE() {
        String userAgent = this.headers.get("User-Agent");

        if(StringUtils.isNotBlank(userAgent)) {
            userAgent = userAgent.toUpperCase();

            if(userAgent.contains("MSIE") || userAgent.contains("TRIDENT")) {
                return true;
            }
        }

        return false;
    }

    public String getParam(String name) {
        return getParam(name, null);
    }

    public String getParam(String name, Charset charset) {
        if(null == charset) {
            charset = Charset.forName(CharsetUtil.ISO_8859_1.name());
        }

        String destCharset = CharsetUtil.UTF_8.name();

        if(isIE()) {
            // IE浏览器GET请求使用GBK编码
            destCharset = Charset.forName("GBK").name();
        }

        String value = this.params.get(name).toString();

        if(HttpMethod.GET == this.method) {
            try {
                value = new String(value.getBytes(charset.name()), destCharset);
            }
            catch(Exception ex) {
                // do nothing
            }
        }

        return value;
    }
}
