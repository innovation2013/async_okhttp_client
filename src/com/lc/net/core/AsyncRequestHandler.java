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

import java.io.IOException;

import com.lc.net.core.http.HttpRequestBase;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncRequestHandler {
	private OkHttpClient okHttpClient;
	private ResponseHandlerInterface responseHandlerInterface;
	private Call call;

	public AsyncRequestHandler(OkHttpClient client, ResponseHandlerInterface responseHandlerInterface) {
		this.responseHandlerInterface = responseHandlerInterface;
		this.okHttpClient = client;
	}

	public void cancel() {
		if (call.isExecuted() && call != null) {
			call.cancel();
			if (null != responseHandlerInterface)
				responseHandlerInterface.senCancelMessage();
		}
	};

	public AsyncRequestHandler excute(final HttpRequestBase httpRequestBase) {
		final Request request = httpRequestBase.getRequest();
		if (httpRequestBase.hasPostMultipartEntity() && responseHandlerInterface instanceof UploadProgressListener
				|| responseHandlerInterface instanceof DownloadProgressListener) {
			this.okHttpClient = AsyncOkhttpClient.newInstance();
			this.okHttpClient.networkInterceptors().add(new Interceptor() {

				@Override
				public Response intercept(Chain chain) throws IOException {
					Response originalResponse = null;
					if (httpRequestBase.hasPostMultipartEntity()
							&& responseHandlerInterface instanceof UploadProgressListener) {
						final UploadProgressListener listener = (UploadProgressListener) responseHandlerInterface;
						originalResponse = chain.proceed(chain.request().newBuilder().method(chain.request().method(),
								new ProgressRequestBody(httpRequestBase.getRequestBody(), new UploadProgressListener() {

									@Override
									public void onUploadProgress(long current, long total, boolean finish) {
										listener.onUploadProgress(current, total, finish);

									}
								})).build());
					} else {
						originalResponse = chain.proceed(chain.request());
					}
					if (responseHandlerInterface instanceof DownloadProgressListener) {
						final DownloadProgressListener listener = (DownloadProgressListener) responseHandlerInterface;
						return originalResponse.newBuilder()
								.body(new ProgressResponseBody(originalResponse.body(), new DownloadProgressListener() {

									@Override
									public void onDownloadProgress(long current, long total, boolean finish) {
										listener.onDownloadProgress(current, total, finish);
									}
								})).build();
					} else {
						return originalResponse;
					}

				}
			});
		}

		this.call = okHttpClient.newCall(request);
		if (null != responseHandlerInterface)
			responseHandlerInterface.sendStartMessage(this);
		this.call.enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response response) throws IOException {
				if (response.isSuccessful()) {
					if (null != responseHandlerInterface)
						responseHandlerInterface.sendSuccessMessage(response);
				} else {
					if (null != responseHandlerInterface)
						responseHandlerInterface.senFailureMessage(response.code(), new Exception(response.message()));
				}
				if (null != responseHandlerInterface)
					responseHandlerInterface.senFinishMessage();

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				if (null != responseHandlerInterface) {
					responseHandlerInterface.senFailureMessage(-1, arg1);
					responseHandlerInterface.senFinishMessage();
				}
			}
		});
		return this;
	};
}
