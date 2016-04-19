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
package com.lc.net.core.handler;

import java.io.IOException;

import okhttp3.Response;

public abstract class TextResponseHandler extends ResponseHandlerBase {

	@Override
	public void sendSuccessMessage(Response response) {
		try {
			final String str = response.body().string();
			postOnMainThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					onSuccess(str);
				}
			});
		} catch (IOException e) {
			senFailureMessage(-1, e);
		}
	}

	public abstract void onSuccess(String text);

}
