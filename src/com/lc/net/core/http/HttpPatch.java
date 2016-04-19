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

import com.lc.net.core.HttpHeaders;
import com.lc.net.core.RequestParams;

import okhttp3.Request;

public class HttpPatch extends HttpRequestBase{

	public HttpPatch(String url) {
		this(url,null);
	}
	public HttpPatch(String url,RequestParams requestParams) {
		this(url,requestParams,null);
	}
	public HttpPatch(String url,RequestParams requestParams,HttpHeaders headers) {
		super(url,requestParams,headers);
	}
	@Override
	public Request buildRequest() {
		// TODO Auto-generated method stub
		return getRequestBuilder().patch(getRequestBody()).build();
	}

}
