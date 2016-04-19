package com.lc.net.core.handler;

import com.lc.net.core.AsyncRequestHandler;
import com.lc.net.core.ResponseHandlerInterface;

import android.os.Handler;
import android.os.Looper;

public abstract class ResponseHandlerBase implements ResponseHandlerInterface {

	private Handler handler = new Handler(Looper.getMainLooper());

	@Override
	public void sendStartMessage(final AsyncRequestHandler asyncRequestHandler) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				onStart(asyncRequestHandler);
			}
		});
	}

	@Override
	public void senCancelMessage() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				onCancel();
			}
		});
	}

	@Override
	public void senFailureMessage(final int code, final Exception exception) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				onFailure(code, exception);
			}
		});
	}

	@Override
	public void senFinishMessage() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				onFinish();
			}
		});
	}

	public void postOnMainThread(Runnable runnable) {
		handler.post(runnable);
	}

	protected void onStart(AsyncRequestHandler asyncRequestHandler) {
	};

	protected void onCancel() {
	};

	protected void onFinish() {
	};

	public abstract void onFailure(int code, Exception exception);

}
