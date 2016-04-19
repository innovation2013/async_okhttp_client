# async_okhttp_client
An Android async httpclient  based on okhttp

#Support
* HTTP Get Request
* HTTP Post Request
* HTTP Put Request
* HTTP Patch Request
* HTTP Head Request
* HTTP Delete Request
* Https
* File Download
* File Upload
* Multipart Request
* Cancel Request
* Cookie Persistence

#Simple Use
*Get a String from server

  	```java
  		AsyncOkhttpClient.getInstance(context).getAsync(url, new TextResponseHandler() {
			
			@Override
			public void onFailure(int code, Exception exception) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(String text) {
				// TODO Auto-generated method stub
				
			}
		});
	```
*Get a Gson Object from server

  	```java
  		AsyncOkhttpClient.getInstance(context).getAsync(url,new JsonResponseHandler<UserModel>() {

			@Override
			public void onSuccess(UserModel t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int code, Exception exception) {
				// TODO Auto-generated method stub
				
			}
		});
  	```
*Post params request

  	```java
		RequestParams requestParams= new RequestParams();
		requestParams.add("name", value);
		requestParams.add("age", value);
		AsyncOkhttpClient.getInstance(this).postAsync(url, requestParams, new JsonResponseHandler<UserModel>() {

			@Override
			public void onSuccess(UserModel t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int code, Exception exception) {
				// TODO Auto-generated method stub
				
			}
		});
	```
*Post file request,you can also use the handler implements DownloadProgressListener to listen progress

  	```java
		RequestParams requestParams= new RequestParams();
		requestParams.add("filename", file);
		AsyncOkhttpClient.getInstance(this).postAsync(url, requestParams, new JsonResponseHandler<Object>() {

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int code, Exception exception) {
				// TODO Auto-generated method stub
				
			}
		});
	```
*Download an file from the server

	```java
			AsyncOkhttpClient.getInstance(this).getAsync("http://www.google.com", new FileResponseHandler(file) {
			
			@Override
			public void onFailure(int code, Exception exception) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(File file) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgress(long current, long total, boolean finish) {
				// TODO Auto-generated method stub
				
			}
		});
	```
