package com.stringbitking.noidea;

import java.util.List;

import com.stringbitking.noidea.actionbar.ActionBarActivity;
import com.stringbitking.noidea.models.Category;
import com.stringbitking.noidea.network.HttpRequesterAsync;
import com.stringbitking.noidea.network.IJSONHandler;
import com.stringbitking.noidea.network.JSONParser;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchActivity extends ActionBarActivity implements IJSONHandler {

	public static final String CATEGORY_ID = "com.stringbitking.noidea.suggestion_id";
	public static final String MIN_RATING = "com.stringbitking.noidea.min_rating";
	public static final String MAX_RATING = "com.stringbitking.noidea.max_rating";

	RatingBar minRatingBar;
	RatingBar maxRatingBar;
	private Float currentMinRating;
	private Float currentMaxRating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		HttpRequesterAsync.getJSONAsync(this, Constants.CATEGORIES_URL);

		minRatingBar = (RatingBar) findViewById(R.id.minRatingBar);
		maxRatingBar = (RatingBar) findViewById(R.id.maxRatingBar);

		setupRatingBars();
	}

	private void setupRatingBars() {
		minRatingBar
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {

						if (rating >= maxRatingBar.getRating()) {
							Toast.makeText(getBaseContext(),
									"Min cannot be greater than max.",
									Toast.LENGTH_SHORT).show();
							ratingBar.setRating(currentMinRating);
						} else {
							currentMinRating = rating;
						}
					}
				});

		maxRatingBar
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						if (rating <= minRatingBar.getRating()) {
							Toast.makeText(getBaseContext(),
									"Min cannot be greater than max.",
									Toast.LENGTH_SHORT).show();
							ratingBar.setRating(currentMaxRating);
						} else {
							currentMaxRating = rating;
						}
					}
				});

		currentMinRating = 0f;
		currentMaxRating = 5f;
	}

	public void onClickTellMe(View view) {

		Spinner spinner = (Spinner) findViewById(R.id.categoriesSpinner);

		Category selectedCategory = (Category) spinner.getSelectedItem();

		Intent newIntent = new Intent(this, SuggestionsListActivity.class);

		newIntent.putExtra(CATEGORY_ID, selectedCategory.getId());
		newIntent.putExtra(MIN_RATING, currentMinRating);
		newIntent.putExtra(MAX_RATING, currentMaxRating);

		startActivity(newIntent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);

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

		if (isActivityCalled) {
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	private void loadCategories() {

		Spinner spinner = (Spinner) findViewById(R.id.categoriesSpinner);

		CategoriesAdapter spinnerAdapter = new CategoriesAdapter(this,
				CategoriesProvider.get().getCategoriesList(), true);

		spinner.setAdapter(spinnerAdapter);

	}

	@Override
	public void parseJSON(String json, int requestCode) {
		List<Category> categories = JSONParser.parseCategories(json);

		CategoriesProvider categoriesProvider = CategoriesProvider.get();
		categoriesProvider.update(categories);

		loadCategories();
	}

}
