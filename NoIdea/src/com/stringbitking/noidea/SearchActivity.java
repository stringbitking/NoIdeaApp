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

import com.stringbitking.noidea.actionbar.ActionBarActivity;
import com.stringbitking.noidea.models.Category;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SearchActivity extends ActionBarActivity {

	public static final String CATEGORY_ID = "com.stringbitking.noidea.suggestion_id";

	private static String categoriesUrl = Constants.CATEGORIES_URL;

	private List<Category> categories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new GetCategoriesJSON().execute();
		
	}

	private void loadCategories() {

		Spinner spinner = (Spinner) findViewById(R.id.categoriesSpinner);
		
		CategoriesAdapter spinnerAdapter = new CategoriesAdapter(this, 
					CategoriesProvider.get().getCategoriesList(), true);
		
		spinner.setAdapter(spinnerAdapter);

	}

	private class GetCategoriesJSON extends AsyncTask<String, String, String> {

		protected String doInBackground(String... arg0) {

			String result = HttpRequester.GetJSON(categoriesUrl);

			return result;

		}

		protected void onPostExecute(String result) {

			parseCategoriesJSON(result);
			
			CategoriesProvider categoriesProvider = CategoriesProvider.get();
			categoriesProvider.update(categories);
			
			loadCategories();

		}

	}
	
	private void parseCategoriesJSON(String result) {
		
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
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Boolean isActivityCalled = false;
		Intent intent = new Intent();

		switch (item.getItemId()) {
		
		case R.id.menu_home:
			intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			isActivityCalled = true;
			break;

		case R.id.menu_search:
			break;

		case R.id.menu_new:
			intent = new Intent(this, PostSuggestionActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			isActivityCalled = true;
			break;
			
		case R.id.menu_favourite:
			intent = new Intent(this, FavouritesListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			isActivityCalled = true;
			break;

		}
		
		if(isActivityCalled) {
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

}
