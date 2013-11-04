package com.stringbitking.noidea;

import android.support.v4.app.Fragment;

public class SuggestionsListActivity extends FragmentActivityBuilder {

	@Override
	protected Fragment createFragment() {
		return new FragmentSuggestionList();
	}

}