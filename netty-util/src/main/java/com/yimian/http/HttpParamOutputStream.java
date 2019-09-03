/**
 *
 */
package com.yimian.http;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

public class HttpParamOutputStream extends ByteArrayOutputStream {
    private String charset;

    public HttpParamOutputStream(String charset) {
        super();
        this.charset = charset;
    }

    public Map<String, String> toParameters() {
        try {
            String content = this.toString(charset);
            return HttpHelper.parseParameters(content);
        }
        catch(UnsupportedEncodingException e) {
        }
        return Collections.EMPTY_MAP;
    }
}
