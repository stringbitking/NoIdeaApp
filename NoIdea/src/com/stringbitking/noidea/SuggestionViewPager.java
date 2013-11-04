package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class SuggestionViewPager extends FragmentActivity {

	private ViewPager theViewPager;

	private ArrayList<Suggestion> suggestionsList;

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);

		theViewPager = new ViewPager(this);

		theViewPager.setId(R.id.viewPager);

		// Set the current View for the ViewPager

		setContentView(theViewPager);
		
		suggestionsList = CurrentSuggestions.get(this).getSuggestionsList();

		// The FragmentManager ads Fragments to Activity views

		FragmentManager fragManager = getSupportFragmentManager();

		theViewPager.setAdapter(new FragmentStatePagerAdapter(fragManager) {

			@Override
			public Fragment getItem(int position) {

				// Gets the specific Suggestion from the right position
				// in the ArrayList

				Suggestion theSuggestion = suggestionsList.get(position);

				// Return a SuggestionFragment by retrieving the id
				// number from the current Suggestion
				
				SuggestionFragment result = (SuggestionFragment) SuggestionFragment
									.newSuggestionFragment(theSuggestion.getIdNumber());
				
				return result;

			}

			// Returns the number of items in the ArrayList

			@Override
			public int getCount() {
				return suggestionsList.size();
			}

		});
		
		UUID contactId = (UUID) getIntent().getSerializableExtra(
				SuggestionFragment.SUGGESTION_ID);

		// Cycle through the Suggestions in the ArrayList to find a match
		// Set the current position of the match in setCurrentItem

		for (int i = 0; i < suggestionsList.size(); i++) {

			if (suggestionsList.get(i).getIdNumber().equals(contactId)) {

				theViewPager.setCurrentItem(i);
				break;

			}

		}

		theViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {

						setTitle("Suggestion #" + position);

					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub

					}
				});
		
	}

}


