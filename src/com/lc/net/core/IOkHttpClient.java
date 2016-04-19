package com.lc.net.core;

import com.lc.net.core.http.HttpRequestBase;

public interface IOkHttpClient {

	AsyncRequestHandler headAsync(String url, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler headAsync(String url, RequestParams params, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler headAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler headAsync(HttpRequestBase httpRequestBase, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler getAsync(String url, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler getAsync(String url, RequestParams params, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler getAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler getAsync(HttpRequestBase httpRequestBase, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler postAsync(String url, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler postAsync(String url, RequestParams params, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler postAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler postAsync(HttpRequestBase requestBase, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler putAsync(String url, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler putAsync(String url, RequestParams params, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler putAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler putAsync(HttpRequestBase httpRequestBase, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler patchAsync(String url, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler patchAsync(String url, RequestParams params, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler patchAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler patchAsync(HttpRequestBase httpRequestBase, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler deleteAsync(String url, ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler deleteAsync(String url, RequestParams params,
			ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler deleteAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface);

	AsyncRequestHandler deleteAsync(HttpRequestBase httpRequestBase, ResponseHandlerInterface responseHandlerInterface);

}