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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.text.TextUtils;
import okhttp3.Response;

public abstract class JsonResponseHandler<T> extends ResponseHandlerBase {
	private Gson gson = new Gson();

	Type getType() {
		Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (type instanceof Class) {
			return type;
		} else {
			return new TypeToken<T>() {
			}.getType();
		}
	}

	private Type mType;

	public JsonResponseHandler() {
		mType = getType();

	}

	@Override
	public void sendSuccessMessage(Response response) {
		try {
			String str = response.body().string();
			if (TextUtils.isEmpty(str)) {
				senFailureMessage(-1, new Exception("response maybe null"));
			} else {
				try {
					final T t = gson.fromJson(str, mType);
					postOnMainThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							onSuccess(t);

						}
					});
				} catch (Exception e) {
					senFailureMessage(-1, e);
				}
			}

		} catch (IOException e) {
			senFailureMessage(-1, e);
		}
	}

	public abstract void onSuccess(T t);
}
