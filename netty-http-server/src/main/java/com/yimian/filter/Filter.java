package com.yimian.filter;

import com.yimian.httpserver.Request;
import com.yimian.httpserver.Response;

public interface Filter {
	void doFilter(Request request, Response response);
}
