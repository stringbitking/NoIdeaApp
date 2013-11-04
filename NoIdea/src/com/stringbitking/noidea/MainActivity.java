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
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String CATEGORY_ID = "com.stringbitking.noidea.suggestion_id";

	private static String categoriesUrl = "http://10.0.3.2:3000/categorieslist";

	private List<Category> categories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new GetCategoriesJSON().execute();
		

	}

	private void loadCategories() {

		Spinner spinner = (Spinner) findViewById(R.id.categoriesSpinner);
		
		CategoriesAdapter spinnerAdapter = new CategoriesAdapter(this, categories);
		
		spinner.setAdapter(spinnerAdapter);

	}

	private class GetCategoriesJSON extends AsyncTask<String, String, String> {

		protected String doInBackground(String... arg0) {

			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());

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
				// BufferedReader reads data from the InputStream until the
				// Buffer is full
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);

				// Will store the data
				StringBuilder theStringBuilder = new StringBuilder();

				String line = null;

				while ((line = reader.readLine()) != null) {

					// Add data from the buffer to the StringBuilder
					theStringBuilder.append(line + "\n");
				}

				// Store the complete data in result
				result = theStringBuilder.toString();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				// Close the InputStream when you're done with it
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception e) {
				}

			}

			try {

				JSONArray arrResult = new JSONArray(result);

				categories = new ArrayList<Category>();

				for (int i = 0; i < arrResult.length(); i++) {

					JSONObject categoryJSON = arrResult.getJSONObject(i);
					
					Category cat = new Category();
					cat.setId(categoryJSON.getString("_id"));
					cat.setName(categoryJSON.getString("name"));
					cat.setVerb(categoryJSON.getString("verb"));
					categories.add(cat);
					
				}

			} catch (JSONException e) {

				e.printStackTrace();

			}

			return result;

		}

		protected void onPostExecute(String result) {

			loadCategories();

		}

	}

	private class CategoriesAdapter implements SpinnerAdapter {

		private List<Category> data;
		private Activity context;
		
		public CategoriesAdapter(Activity context, List<Category> categories) {
			
			this.context = context;
			this.data = categories;
			
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 1;
		}

		@Override
		public int getItemViewType(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
	        textView.setText(data.get(position).getVerb());
	        return textView;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			
			if(data != null && data.size() > 0) {
				return false;
			}
			
			return true;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
	        }
	        ((TextView) convertView).setText(data.get(position).getVerb());
	        return convertView;
		}

	}

	public void onClickTellMe(View view) {

		Spinner spinner = (Spinner) findViewById(R.id.categoriesSpinner);
		
		Category selectedCategory = (Category) spinner.getSelectedItem();

		Intent newIntent = new Intent(this, SuggestionsListActivity.class);

		newIntent.putExtra(CATEGORY_ID, selectedCategory.getId());

		startActivity(newIntent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
