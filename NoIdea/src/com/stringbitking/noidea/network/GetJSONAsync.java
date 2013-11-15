package com.stringbitking.noidea.network;

import android.os.AsyncTask;

public class GetJSONAsync extends AsyncTask<String, String, String> {
	IJSONHandler jsonHandler;
	int requestCode;
	
	public GetJSONAsync(IJSONHandler jsonHandler) {
		super();
		this.jsonHandler = jsonHandler;
		this.requestCode = 0;
	}
	
	public GetJSONAsync(IJSONHandler jsonHandler, int requestCode) {
		super();
		this.jsonHandler = jsonHandler;
		this.requestCode = requestCode;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		String result = HttpRequester.GetJSON(url);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		jsonHandler.parseJSON(result, requestCode);
	}

}
