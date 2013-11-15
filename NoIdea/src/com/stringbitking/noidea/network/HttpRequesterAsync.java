package com.stringbitking.noidea.network;

import java.util.List;

import org.apache.http.NameValuePair;

public class HttpRequesterAsync {
	public static void getJSONAsync(IJSONHandler jsonHandler, String url) {
		new GetJSONAsync(jsonHandler).execute(url);
	}

	public static void getJSONAsync(IJSONHandler jsonHandler, int requestCode,
			String url) {
		new GetJSONAsync(jsonHandler, requestCode).execute(url);
	}

	public static void postJSONAsync(IJSONHandler jsonHandler,
			List<NameValuePair> content, String url) {
		new PostJSONAsync(jsonHandler, content).execute(url);
	}

	public static void postJSONAsync(IJSONHandler jsonHandler,
			List<NameValuePair> content, int requestCode, String url) {
		new PostJSONAsync(jsonHandler, content, requestCode).execute(url);
	}
}
