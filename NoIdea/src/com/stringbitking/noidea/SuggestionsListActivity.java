package com.stringbitking.noidea;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SuggestionsListActivity extends FragmentActivityBuilder {

	@Override
	protected Fragment createFragment() {

		Fragment result = new FragmentSuggestionList();
		Bundle bundle = new Bundle();
		bundle.putString(FragmentSuggestionList.DISPLAY_OPTION,
				FragmentSuggestionList.SINGLE_CATEGORY_OPTION);
		result.setArguments(bundle);

		return result;

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
			intent = new Intent(this, SearchActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			isActivityCalled = true;
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

}