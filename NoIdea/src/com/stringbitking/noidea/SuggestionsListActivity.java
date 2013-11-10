package com.stringbitking.noidea;

import android.os.Bundle;
import android.support.v4.app.Fragment;

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

}