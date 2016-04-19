/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lc.net.core;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import com.lc.net.core.http.HttpDelete;
import com.lc.net.core.http.HttpGet;
import com.lc.net.core.http.HttpHead;
import com.lc.net.core.http.HttpPatch;
import com.lc.net.core.http.HttpPost;
import com.lc.net.core.http.HttpPut;
import com.lc.net.core.http.HttpRequestBase;

import android.content.Context;
import okhttp3.OkHttpClient;

public class AsyncOkhttpClient implements IOkHttpClient {

	private static final int DEFAULT_CONNECT_TIMEOUT = 0xa;
	private static final int DEFAULT_WRITE_TIMEOUT = 0xa;
	private static final int DEFAULT_READ_TIMEOUT = 0xa;

	private OkHttpClient client;
	private static IOkHttpClient instance;
	private static CookieManager cookieManager;
	private PersistentCookieStore persistentCookieStore;
	private Context context;

	private AsyncOkhttpClient(Context context) {
		this.context = context;

		if (null == persistentCookieStore)
			persistentCookieStore = new PersistentCookieStore(context);
		if (null == cookieManager)
			cookieManager = new CookieManager(persistentCookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
		if (null == client)
			client = newInstance();

	}

	public static OkHttpClient newInstance() {
		return new OkHttpClient.Builder().connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
				.writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS).cookieJar(new JavaNetCookieJar(cookieManager))
				.cache(null).hostnameVerifier(new HostnameVerifier() {

					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				}).build();
	}

	public static void destoryInstance() {
		synchronized (AsyncOkhttpClient.class) {
			if (null != instance) {
				((AsyncOkhttpClient) instance).destory();
			}
		}
	}

	private void destory() {
		client = null;
		cookieManager = null;
		persistentCookieStore = null;
	}

	public static IOkHttpClient getInstance(Context context) {
		if (null == instance) {
			synchronized (AsyncOkhttpClient.class) {
				if (null == instance) {
					instance = new AsyncOkhttpClient(context);
				}
			}
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#headAsync(java.lang.String,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler headAsync(String url, ResponseHandlerInterface responseHandlerInterface) {
		return headAsync(url, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#headAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler headAsync(String url, RequestParams params,
			ResponseHandlerInterface responseHandlerInterface) {
		return headAsync(url, params, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#headAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.HttpHeaders,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler headAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface) {
		return headAsync(new HttpHead(url, params, httpHeaders), responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#headAsync(com.lc.net.core.http.
	 * HttpRequestBase, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler headAsync(HttpRequestBase httpRequestBase,
			ResponseHandlerInterface responseHandlerInterface) {
		return sendAsyncRequest(client, httpRequestBase, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#getAsync(java.lang.String,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler getAsync(String url, ResponseHandlerInterface responseHandlerInterface) {
		return getAsync(url, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#getAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler getAsync(String url, RequestParams params,
			ResponseHandlerInterface responseHandlerInterface) {
		return getAsync(url, params, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#getAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.HttpHeaders,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler getAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface) {
		return getAsync(new HttpGet(url, params, httpHeaders), responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#getAsync(com.lc.net.core.http.
	 * HttpRequestBase, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler getAsync(HttpRequestBase httpRequestBase,
			ResponseHandlerInterface responseHandlerInterface) {
		return sendAsyncRequest(client, httpRequestBase, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#postAsync(java.lang.String,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler postAsync(String url, ResponseHandlerInterface responseHandlerInterface) {
		return postAsync(url, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#postAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler postAsync(String url, RequestParams params,
			ResponseHandlerInterface responseHandlerInterface) {
		return postAsync(url, params, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#postAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.HttpHeaders,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler postAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface) {
		return postAsync(new HttpPost(url, params, httpHeaders), responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#postAsync(com.lc.net.core.http.
	 * HttpRequestBase, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler postAsync(HttpRequestBase requestBase,
			ResponseHandlerInterface responseHandlerInterface) {
		return sendAsyncRequest(client, requestBase, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#putAsync(java.lang.String,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler putAsync(String url, ResponseHandlerInterface responseHandlerInterface) {
		return putAsync(url, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#putAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler putAsync(String url, RequestParams params,
			ResponseHandlerInterface responseHandlerInterface) {
		return putAsync(url, params, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#putAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.HttpHeaders,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler putAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface) {
		return putAsync(new HttpPut(url, params, httpHeaders), responseHandlerInterface);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#putAsync(com.lc.net.core.http.
	 * HttpRequestBase, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler putAsync(HttpRequestBase httpRequestBase,
			ResponseHandlerInterface responseHandlerInterface) {
		return sendAsyncRequest(client, httpRequestBase, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#patchAsync(java.lang.String,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler patchAsync(String url, ResponseHandlerInterface responseHandlerInterface) {
		return patchAsync(url, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#patchAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler patchAsync(String url, RequestParams params,
			ResponseHandlerInterface responseHandlerInterface) {
		return patchAsync(url, params, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#patchAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.HttpHeaders,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler patchAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface) {
		return patchAsync(new HttpPatch(url, params, httpHeaders), responseHandlerInterface);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#patchAsync(com.lc.net.core.http.
	 * HttpRequestBase, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler patchAsync(HttpRequestBase httpRequestBase,
			ResponseHandlerInterface responseHandlerInterface) {
		return sendAsyncRequest(client, httpRequestBase, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#deleteAsync(java.lang.String,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler deleteAsync(String url, ResponseHandlerInterface responseHandlerInterface) {
		return deleteAsync(url, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#deleteAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler deleteAsync(String url, RequestParams params,
			ResponseHandlerInterface responseHandlerInterface) {
		return deleteAsync(url, params, null, responseHandlerInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#deleteAsync(java.lang.String,
	 * com.lc.net.core.RequestParams, com.lc.net.core.HttpHeaders,
	 * com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler deleteAsync(String url, RequestParams params, HttpHeaders httpHeaders,
			ResponseHandlerInterface responseHandlerInterface) {
		return deleteAsync(new HttpDelete(url, params), responseHandlerInterface);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lc.net.core.IOkHttpClient#deleteAsync(com.lc.net.core.http.
	 * HttpRequestBase, com.lc.net.core.ResponseHandlerInterface)
	 */
	@Override
	public AsyncRequestHandler deleteAsync(HttpRequestBase httpRequestBase,
			ResponseHandlerInterface responseHandlerInterface) {
		return sendAsyncRequest(client, httpRequestBase, responseHandlerInterface);

	}

	private AsyncRequestHandler sendAsyncRequest(OkHttpClient client, HttpRequestBase httpRequestBase,
			ResponseHandlerInterface responseHandlerInterface) {
		return new AsyncRequestHandler(client, responseHandlerInterface).excute(httpRequestBase);
	}
}
