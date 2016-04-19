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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RequestParams {

	public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";

	public final static String APPLICATION_JSON = "application/json";

	protected final static String LOG_TAG = "RequestParams";
	protected final ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<String, String>();
	protected final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<String, FileWrapper>();
	protected final ConcurrentHashMap<String, List<FileWrapper>> fileArrayParams = new ConcurrentHashMap<String, List<FileWrapper>>();
	protected final ConcurrentHashMap<String, Object> urlParamsWithObjects = new ConcurrentHashMap<String, Object>();
	protected boolean forceMultipartEntity = false;
	protected String elapsedFieldInJsonStreamer = "_elapsed";
	protected boolean autoCloseInputStreams;
	private boolean hasPostMultipartEntity= false;
	

	public boolean hasPostMultipartEntity(){
		return hasPostMultipartEntity;
	}
	
	/**
	 * Constructs a new empty {@code RequestParams} instance.
	 */
	public RequestParams() {
		this((Map<String, String>) null);
	}

	/**
	 * Constructs a new RequestParams instance containing the key/value string
	 * params from the specified map.
	 *
	 * @param source
	 *            the source key/value string map to add.
	 */
	public RequestParams(Map<String, String> source) {
		if (source != null) {
			for (Map.Entry<String, String> entry : source.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Constructs a new RequestParams instance and populate it with a single
	 * initial key/value string param.
	 *
	 * @param key
	 *            the key name for the intial param.
	 * @param value
	 *            the value string for the initial param.
	 */
	public RequestParams(final String key, final String value) {
		this(new HashMap<String, String>() {
			{
				put(key, value);
			}
		});
	}

	/**
	 * Constructs a new RequestParams instance and populate it with multiple
	 * initial key/value string param.
	 *
	 * @param keysAndValues
	 *            a sequence of keys and values. Objects are automatically
	 *            converted to Strings (including the value {@code null}).
	 * @throws IllegalArgumentException
	 *             if the number of arguments isn't even.
	 */
	public RequestParams(Object... keysAndValues) {
		int len = keysAndValues.length;
		if (len % 2 != 0)
			throw new IllegalArgumentException("Supplied arguments must be even");
		for (int i = 0; i < len; i += 2) {
			String key = String.valueOf(keysAndValues[i]);
			String val = String.valueOf(keysAndValues[i + 1]);
			put(key, val);
		}
	}



	/**
	 * If set to true will force Content-Type header to `multipart/form-data`
	 * even if there are not Files or Streams to be send
	 * <p>
	 * &nbsp;
	 * </p>
	 * Default value is false
	 *
	 * @param force
	 *            boolean, should declare content-type multipart/form-data even
	 *            without files or streams present
	 */
	public void setForceMultipartEntityContentType(boolean force) {
		this.forceMultipartEntity = force;
	}

	/**
	 * Adds a key/value string pair to the request.
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value string for the new param.
	 */
	public void put(String key, String value) {
		if (key != null && value != null) {
			urlParams.put(key, value);
		}
	}

	/**
	 * Adds files array to the request.
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param files
	 *            the files array to add.
	 * @throws FileNotFoundException
	 *             if one of passed files is not found at time of assembling the
	 *             requestparams into request
	 */
	public void put(String key, File files[]) throws FileNotFoundException {
		put(key, files, null, null);
	}

	/**
	 * Adds files array to the request with both custom provided file
	 * content-type and files name
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param files
	 *            the files array to add.
	 * @param contentType
	 *            the content type of the file, eg. application/json
	 * @param customFileName
	 *            file name to use instead of real file name
	 * @throws FileNotFoundException
	 *             throws if wrong File argument was passed
	 */
	public void put(String key, File files[], String contentType, String customFileName) throws FileNotFoundException {

		if (key != null) {
			List<FileWrapper> fileWrappers = new ArrayList<FileWrapper>();
			for (File file : files) {
				if (file == null || !file.exists()) {
					throw new FileNotFoundException();
				}
				fileWrappers.add(new FileWrapper(file, contentType, customFileName));
			}
			fileArrayParams.put(key, fileWrappers);
		}
	}

	/**
	 * Adds a file to the request.
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 * @throws FileNotFoundException
	 *             throws if wrong File argument was passed
	 */
	public void put(String key, File file) throws FileNotFoundException {
		put(key, file, null, null);
	}

	/**
	 * Adds a file to the request with custom provided file name
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 * @param customFileName
	 *            file name to use instead of real file name
	 * @throws FileNotFoundException
	 *             throws if wrong File argument was passed
	 */
	public void put(String key, String customFileName, File file) throws FileNotFoundException {
		put(key, file, null, customFileName);
	}

	/**
	 * Adds a file to the request with custom provided file content-type
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 * @param contentType
	 *            the content type of the file, eg. application/json
	 * @throws FileNotFoundException
	 *             throws if wrong File argument was passed
	 */
	public void put(String key, File file, String contentType) throws FileNotFoundException {
		put(key, file, contentType, null);
	}

	/**
	 * Adds a file to the request with both custom provided file content-type
	 * and file name
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 * @param contentType
	 *            the content type of the file, eg. application/json
	 * @param customFileName
	 *            file name to use instead of real file name
	 * @throws FileNotFoundException
	 *             throws if wrong File argument was passed
	 */
	public void put(String key, File file, String contentType, String customFileName) throws FileNotFoundException {
		if (file == null || !file.exists()) {
			throw new FileNotFoundException();
		}
		if (key != null) {
			fileParams.put(key, new FileWrapper(file, contentType, customFileName));
		}
	}


	/**
	 * Adds param with non-string value (e.g. Map, List, Set).
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the non-string value object for the new param.
	 */
	public void put(String key, Object value) {
		if (key != null && value != null) {
			urlParamsWithObjects.put(key, value);
		}
	}

	/**
	 * Adds a int value to the request.
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value int for the new param.
	 */
	public void put(String key, int value) {
		if (key != null) {
			urlParams.put(key, String.valueOf(value));
		}
	}

	/**
	 * Adds a long value to the request.
	 *
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value long for the new param.
	 */
	public void put(String key, long value) {
		if (key != null) {
			urlParams.put(key, String.valueOf(value));
		}
	}

	/**
	 * Adds string value to param which can have more than one value.
	 *
	 * @param key
	 *            the key name for the param, either existing or new.
	 * @param value
	 *            the value string for the new param.
	 */
	@SuppressWarnings("unchecked")
	public void add(String key, String value) {
		if (key != null && value != null) {
			Object params = urlParamsWithObjects.get(key);
			if (params == null) {
				// Backward compatible, which will result in "k=v1&k=v2&k=v3"
				params = new HashSet<String>();
				this.put(key, params);
			}
			if (params instanceof List) {
				((List<Object>) params).add(value);
			} else if (params instanceof Set) {
				((Set<Object>) params).add(value);
			}
		}
	}

	/**
	 * Removes a parameter from the request.
	 *
	 * @param key
	 *            the key name for the parameter to remove.
	 */
	public void remove(String key) {
		urlParams.remove(key);
//		streamParams.remove(key);
		fileParams.remove(key);
		urlParamsWithObjects.remove(key);
		fileArrayParams.remove(key);
	}

	/**
	 * Check if a parameter is defined.
	 *
	 * @param key
	 *            the key name for the parameter to check existence.
	 * @return Boolean
	 */
	public boolean has(String key) {
		return urlParams.get(key) != null || fileParams.get(key) != null
				|| urlParamsWithObjects.get(key) != null || fileArrayParams.get(key) != null;
	}



	/**
	 * Sets an additional field when upload a JSON object through the streamer
	 * to hold the time, in milliseconds, it took to upload the payload. By
	 * default, this field is set to "_elapsed".
	 * <p>
	 * &nbsp;
	 * </p>
	 * To disable this feature, call this method with null as the field value.
	 *
	 * @param value
	 *            field name to add elapsed time, or null to disable
	 */
	public void setElapsedFieldInJsonStreamer(String value) {
		this.elapsedFieldInJsonStreamer = value;
	}

	/**
	 * Set global flag which determines whether to automatically close input
	 * streams on successful upload.
	 *
	 * @param flag
	 *            boolean whether to automatically close input streams
	 */
	public void setAutoCloseInputStreams(boolean flag) {
		autoCloseInputStreams = flag;
	}
	
	
	public RequestBody getRequestBody(){
		if(!forceMultipartEntity &&  fileParams.isEmpty() && fileArrayParams.isEmpty()){
			hasPostMultipartEntity= false;
			return crateFormRequestBody();
		}else {
			hasPostMultipartEntity= true;
			return createMultipartEntity();
		}
	}
	

	private RequestBody createMultipartEntity() {
		MultipartBody.Builder builder= new MultipartBody.Builder();

        // Add string params
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""), RequestBody.create(null, entry.getValue()));

        }

        // Add non-string params
        List<BasicNameValuePair> params = getParamsList(null, urlParamsWithObjects);
        for (BasicNameValuePair kv : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + kv.key + "\""), RequestBody.create(null, kv.value));

        }


        // Add file params
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            FileWrapper fileWrapper = entry.getValue();
        	RequestBody body = RequestBody.create(MediaType.parse(fileWrapper.contentType),fileWrapper.file);
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\"; filename=\"" + fileWrapper.customFileName + "\""), body);

        }

        // Add file collection
        for (ConcurrentHashMap.Entry<String, List<FileWrapper>> entry : fileArrayParams.entrySet()) {
            List<FileWrapper> fileWrapper = entry.getValue();
            for (FileWrapper fw : fileWrapper) {
            	RequestBody body = RequestBody.create(MediaType.parse(fw.contentType),fw.file);
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\"; filename=\"" + fw.customFileName + "\""), body);
            }
        }

        return builder.build();
	}
	
	
	public String getParamString(){
		hasPostMultipartEntity= false;
		List<BasicNameValuePair> paramsList = getParamsList();
		if(paramsList.isEmpty()){
			return "";
		}
		StringBuilder builder= new StringBuilder();
		for (BasicNameValuePair basicNameValuePair : paramsList) {
			if(builder.length()>0)
				builder.append("&");
			builder.append(basicNameValuePair.key);
			builder.append("=");
			builder.append(basicNameValuePair.value);
		}
		
		return "?"+ builder.toString();
		
		
		
	}

	private RequestBody crateFormRequestBody() {
		FormBody.Builder builder= new FormBody.Builder();
		List<BasicNameValuePair> paramsList = getParamsList();
		for (BasicNameValuePair basicNameValuePair : paramsList) {
			builder.add(basicNameValuePair.key, basicNameValuePair.value);
		}
		
		return builder.build();
	}
	
	
	 protected List<BasicNameValuePair> getParamsList() {
	        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

	        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
	            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	        }

	        lparams.addAll(getParamsList(null, urlParamsWithObjects));

	        return lparams;
	    }

	    private List<BasicNameValuePair> getParamsList(String key, Object value) {
	        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
	        if (value instanceof Map) {
	            Map map = (Map) value;
	            List list = new ArrayList<Object>(map.keySet());
	            // Ensure consistent ordering in query string
	            if (list.size() > 0 && list.get(0) instanceof Comparable) {
	                Collections.sort(list);
	            }
	            for (Object nestedKey : list) {
	                if (nestedKey instanceof String) {
	                    Object nestedValue = map.get(nestedKey);
	                    if (nestedValue != null) {
	                        params.addAll(getParamsList(key == null ? (String) nestedKey : String.format(Locale.US, "%s[%s]", key, nestedKey),
	                                nestedValue));
	                    }
	                }
	            }
	        } else if (value instanceof List) {
	            List list = (List) value;
	            int listSize = list.size();
	            for (int nestedValueIndex = 0; nestedValueIndex < listSize; nestedValueIndex++) {
	                params.addAll(getParamsList(String.format(Locale.US, "%s[%d]", key, nestedValueIndex), list.get(nestedValueIndex)));
	            }
	        } else if (value instanceof Object[]) {
	            Object[] array = (Object[]) value;
	            int arrayLength = array.length;
	            for (int nestedValueIndex = 0; nestedValueIndex < arrayLength; nestedValueIndex++) {
	                params.addAll(getParamsList(String.format(Locale.US, "%s[%d]", key, nestedValueIndex), array[nestedValueIndex]));
	            }
	        } else if (value instanceof Set) {
	            Set set = (Set) value;
	            for (Object nestedValue : set) {
	                params.addAll(getParamsList(key, nestedValue));
	            }
	        } else {
	            params.add(new BasicNameValuePair(key, value.toString()));
	        }
	        return params;
	    }


	public static class FileWrapper implements Serializable {
		public final File file;
		public final String contentType;
		public final String customFileName;

		public FileWrapper(File file, String contentType, String customFileName) {
			this.file = file;
			this.contentType = contentType;
			this.customFileName = customFileName;
		}
	}

	public static class BasicNameValuePair implements Serializable {
		public final String key;
		public final String value;

		public BasicNameValuePair(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}


}
