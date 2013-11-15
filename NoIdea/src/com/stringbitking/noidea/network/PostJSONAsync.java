package com.stringbitking.noidea.network;

import java.util.List;

import org.apache.http.NameValuePair;

import android.os.AsyncTask;

public class PostJSONAsync extends AsyncTask<String, String, String> {
	List<NameValuePair> content;
	IJSONHandler jsonHandler;
	int requestCode;
	
	public PostJSONAsync(IJSONHandler jsonHandler, List<NameValuePair> content) {
		super();
		this.jsonHandler = jsonHandler;
		this.requestCode = 0;
		this.content = content;
	}
	
	public PostJSONAsync(IJSONHandler jsonHandler, List<NameValuePair> content, int requestCode) {
		super();
		this.jsonHandler = jsonHandler;
		this.requestCode = requestCode;
		this.content = content;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		String result = HttpRequester.PostJSON(url, content);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		jsonHandler.parseJSON(result, requestCode);
	}
}
