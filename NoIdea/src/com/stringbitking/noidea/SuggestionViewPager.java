package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.stringbitking.noidea.models.Suggestion;
import com.stringbitking.noidea.models.User;

public class SuggestionViewPager extends FragmentActivity {

	private ViewPager theViewPager;
	private int currentPosition;
	private Suggestion currentSuggestion;
	private ArrayList<Suggestion> suggestionsList;
	private Boolean isFavourite;

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
						.newSuggestionFragment(theSuggestion.getId());
				Bundle args = result.getArguments();
				String suggestionId = (String) args
						.getSerializable(SuggestionFragment.SUGGESTION_ID);
				getSupportFragmentManager().beginTransaction()
						.add(result, Integer.toString(position)).commit();

				return result;

			}

			// Returns the number of items in the ArrayList

			// @Override
			// public Object instantiateItem(ViewGroup container, int position)
			// {
			// Fragment item = getItem(position);
			// container.addView(item, position);
			// return item;
			// }

			@Override
			public int getCount() {
				return suggestionsList.size();
			}

		});

		String suggestionId = (String) getIntent().getSerializableExtra(
				SuggestionFragment.SUGGESTION_ID);

		// Cycle through the Suggestions in the ArrayList to find a match
		// Set the current position of the match in setCurrentItem

		for (int i = 0; i < suggestionsList.size(); i++) {

			if (suggestionsList.get(i).getId().equals(suggestionId)) {

				theViewPager.setCurrentItem(i);
				currentPosition = i;
				break;

			}

		}

		theViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {

						setTitle("Suggestion #" + position);
						currentPosition = position;
						// new CheckFavouriteAsync().execute();

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

	public void toggleIsFavourite() {
		if (currentSuggestion.getIsFavourite()) {
			currentSuggestion.setIsFavourite(false);
		} else {
			currentSuggestion.setIsFavourite(true);
		}
	}

	public void onClickAddToFavourites(View view) {
		int position = theViewPager.getCurrentItem();
		currentSuggestion = CurrentSuggestions.get(this).getSuggestionsList()
				.get(position);
		new AddRemoveFavouriteAsync().execute();
	}

	private class AddRemoveFavouriteAsync extends
			AsyncTask<String, String, String> {

		protected String doInBackground(String... arg0) {

			// int position = theViewPager.getCurrentItem();
			// Suggestion suggestion = CurrentSuggestions.get();

			List<NameValuePair> content = new ArrayList<NameValuePair>(2);
			content.add(new BasicNameValuePair("suggestionId",
					currentSuggestion.getId()));
			content.add(new BasicNameValuePair("facebookId", User
					.getFacebookId()));
			String addFavouriteUrl = Constants.ADD_FAVOURITE_URL;
			String removeFavouriteUrl = Constants.FAVOURITE_URL + "remove";

			String result;
			Boolean favBool = currentSuggestion.getIsFavourite();
			if (favBool) {
				result = HttpRequester.PostJSON(removeFavouriteUrl, content);
			} else {
				result = HttpRequester.PostJSON(addFavouriteUrl, content);
			}

			return result;

		}

		protected void onPostExecute(String result) {
			toggleIsFavourite();
			updateFavouriteImage();
		}

	}

	private void updateFavouriteImage() {
		
		String index = Integer.toString(theViewPager.getCurrentItem());
		View currentPage = getSupportFragmentManager().findFragmentByTag(index).getView();
		ImageView favouriteImageView = (ImageView) currentPage
				.findViewById(R.id.favouriteImageView);
		TextView textView = (TextView) currentPage
				.findViewById(R.id.fragmentSuggestionTitleTextView);
		String text = textView.getText().toString();
		Drawable drawable;
		if (currentSuggestion.getIsFavourite()) {
			drawable = getResources().getDrawable(R.drawable.heart);
		} else {
			drawable = getResources().getDrawable(R.drawable.white_heart);
		}

		favouriteImageView.setImageDrawable(drawable);
	}
	
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    int index = theViewPager.getCurrentItem();
	    currentSuggestion = suggestionsList.get(index);
	    String title = currentSuggestion.getTitle();
	    String description = currentSuggestion.getDescription();
//	    String imageUrl = Constants.SERVER_URL + "images/" + currentSuggestion.getImage();
	    String imageUrl = "http://stringbitking.hostbg.net/wp-content/uploads/2013/03/ClassDiagram.png";
	    params.putString("name", title);
	    params.putString("caption", "Lets watch.");
	    params.putString("description", description);
	    params.putString("link", "https://developers.facebook.com/android");
//	    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
	    params.putString("picture", imageUrl);

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(this,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getApplicationContext(),
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	
	public void onClickShareSomething(View view) {
		
		publishFeedDialog();
		
	}

}
