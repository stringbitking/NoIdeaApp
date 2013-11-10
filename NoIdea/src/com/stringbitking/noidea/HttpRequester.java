package com.stringbitking.noidea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

public class HttpRequester {

	public static String GetJSON(String url) {

		StringBuilder theStringBuilder = new StringBuilder();

		DefaultHttpClient httpclient = new DefaultHttpClient(
				new BasicHttpParams());

		HttpGet httpget = new HttpGet(url);

		httpget.setHeader("Content-type", "application/json");

		// Used to read data from the URL
		InputStream inputStream = null;

		try {

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);

			String line = null;

			while ((line = reader.readLine()) != null) {
				theStringBuilder.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// Close the InputStream
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
			}

		}

		return theStringBuilder.toString();

	}

	public static String PostJSON(String url, List<NameValuePair> content) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
//		httppost.setHeader("Content-type", "application/json");
		StringBuilder theStringBuilder = new StringBuilder();
		InputStream inputStream = null;

		try {
			httppost.setEntity(new UrlEncodedFormEntity(content));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);

			String line = null;

			while ((line = reader.readLine()) != null) {
				theStringBuilder.append(line + "\n");
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// Close the InputStream
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
			}

		}

		String result = theStringBuilder.toString();
		return result;
	}

}
