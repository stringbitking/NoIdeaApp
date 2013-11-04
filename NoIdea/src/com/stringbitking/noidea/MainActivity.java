package com.stringbitking.noidea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

	public static final String CATEGORY_ID = "com.stringbitking.noidea.suggestion_id";
	
	private static String categoriesUrl = "http://10.0.3.2:3000/categorieslist";
	
	private List<String> categoryVerbs;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		new GetCategoriesJSON().execute();
        
    }

    private void loadCategories() {

    	Spinner spinner = (Spinner) findViewById(R.id.categoriesSpinner);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, 
								android.R.layout.simple_spinner_dropdown_item, categoryVerbs);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		
	}

	private class GetCategoriesJSON extends AsyncTask<String, String, String> {

		protected String doInBackground(String... arg0) {

			DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
			
			HttpGet httppost = new HttpGet(categoriesUrl);
			
			httppost.setHeader("Content-type", "application/json");

			// Used to read data from the URL
			InputStream inputStream = null;
			
			// Will hold all the data gathered from the URL
			String result = null;

			try {
				
				HttpResponse response = httpclient.execute(httppost);        
				
				// The content from the requested URL along with headers, etc.
				HttpEntity entity = response.getEntity();

				// Get the main content from the URL
				inputStream = entity.getContent();
				
				// JSON is UTF-8 by default
				// BufferedReader reads data from the InputStream until the Buffer is full
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				
				// Will store the data
				StringBuilder theStringBuilder = new StringBuilder();

				String line = null;
				
				while ((line = reader.readLine()) != null)
				{
					
					// Add data from the buffer to the StringBuilder
					theStringBuilder.append(line + "\n");
				}
				
				// Store the complete data in result
				result = theStringBuilder.toString();

			} catch (Exception e) { 
				e.printStackTrace();
			}
			finally {
				
				// Close the InputStream when you're done with it
				try{if(inputStream != null)inputStream.close();}
				catch(Exception e){}
				
			}

			try {
				
				JSONArray arrResult = new JSONArray(result);
				
				categoryVerbs = new ArrayList<String>();
				categoryVerbs.add("do");

				for(int i = 0; i < arrResult.length(); i++) {
					
					JSONObject category = arrResult.getJSONObject(i);
					Log.e("INFO", category.getString("name"));
					categoryVerbs.add(category.getString("verb"));
					
				}
				

			} 
			catch (JSONException e) {

				e.printStackTrace();
				
			}

			return result;

		}

		protected void onPostExecute(String result){

			loadCategories();

		}

	}
	
	public void onClickTellMe(View view) {
		
		Intent newIntent = new Intent(this, SuggestionsListActivity.class);

		newIntent.putExtra(CATEGORY_ID, 1);

		startActivity(newIntent);
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
