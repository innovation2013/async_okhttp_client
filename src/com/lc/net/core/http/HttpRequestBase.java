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
package com.lc.net.core.http;

import java.util.Map.Entry;

import com.lc.net.core.HttpHeaders;
import com.lc.net.core.RequestParams;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;

public abstract class HttpRequestBase {
	private HttpHeaders headers;
	private String url;
	private RequestParams requestParams;
	private Builder builder;

	public HttpRequestBase(String url) {
		this(url, null);
	}

	public HttpRequestBase(String url, RequestParams requestParams) {
		this(url, requestParams, null);
	}

	public HttpRequestBase(String url, RequestParams requestParams, HttpHeaders headers) {
		this.headers = headers;
		this.url = url;
		this.requestParams = requestParams;
		builder = new Builder().url(url);
	}

	public boolean hasPostMultipartEntity() {
		return requestParams == null ? true : requestParams.hasPostMultipartEntity();
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setRequestParams(RequestParams requestParams) {
		this.requestParams = requestParams;
	}

	public RequestParams getRequestParams() {
		return requestParams;
	}

	public RequestBody getRequestBody() {
		if (null != requestParams)
			return requestParams.getRequestBody();
		return null;
	}

	public void addHeader(String key, String value) {
		if (this.headers == null)
			this.headers = new HttpHeaders();
		this.headers.put(key, value);
	}

	public Request getRequest() {
		buildHeaders();
		return buildRequest();
	}

	public Headers buildHeaders() {
		okhttp3.Headers.Builder b = new Headers.Builder();
		if (null != headers) {
			for (Entry<String, String> iterable_element : headers.entrySet()) {
				b.add(iterable_element.getKey(), iterable_element.getValue());
			}
		}
		Headers h = b.build();
		builder.headers(h);
		return h;
	}

	public abstract Request buildRequest();

	public Builder getRequestBuilder() {
		return builder;
	}

}
