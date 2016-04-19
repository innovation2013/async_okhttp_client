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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.lc.net.core.DownloadProgressListener;

import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public abstract class FileResponseHandler extends ResponseHandlerBase implements DownloadProgressListener {

	private File file;

	public FileResponseHandler(File file) {
		this.file = file;
	}

	public FileResponseHandler(String fileName, String path) throws IOException {
		File dir = new File(path);
		if (!dir.exists()) {
			boolean flag = dir.mkdirs();
			if (!flag) {
				throw new IOException(fileName + "  could not be create");
			}
		}
		this.file = new File(dir, fileName);
	}

	@Override
	public void sendSuccessMessage(Response response) {
		try {
			BufferedSink sink = Okio.buffer(Okio.sink(file));
			sink.writeAll(response.body().source());
			sink.flush();
			sink.close();
			postOnMainThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					onSuccess(file);

				}
			});

		} catch (Exception e) {
			senFailureMessage(-1,e);
		}
	}

	public abstract void onProgress(long current, long total, boolean finish);

	public abstract void onSuccess(File file);

	@Override
	public void onDownloadProgress(long current, long total, boolean finish) {
		this.onProgress(current, total, finish);
	}

}
