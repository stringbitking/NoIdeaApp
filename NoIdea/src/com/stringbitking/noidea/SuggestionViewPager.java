package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.stringbitking.noidea.models.Suggestion;
import com.stringbitking.noidea.models.User;
import com.stringbitking.noidea.network.HttpRequesterAsync;
import com.stringbitking.noidea.network.IJSONHandler;

public class SuggestionViewPager extends FragmentActivity implements
		IJSONHandler {

	private static final int FLAG_REQUEST_CODE = 1;
	private static final int FAVOURITE_REQUEST_CODE = 2;

	private ViewPager theViewPager;
	private Suggestion currentSuggestion;
	private ArrayList<Suggestion> suggestionsList;

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);

		theViewPager = new ViewPager(this);

		theViewPager.setId(R.id.viewPager);

		// Set the current View for the ViewPager
		setContentView(theViewPager);

		suggestionsList = CurrentSuggestions.get(this).getSuggestionsList();

		FragmentManager fragManager = getSupportFragmentManager();

		theViewPager.setAdapter(new FragmentStatePagerAdapter(fragManager) {

			@Override
			public Fragment getItem(int position) {
				Suggestion theSuggestion = suggestionsList.get(position);
				SuggestionFragment result = (SuggestionFragment) SuggestionFragment
						.newSuggestionFragment(theSuggestion.getId());

				getSupportFragmentManager().beginTransaction()
						.add(result, Integer.toString(position)).commit();

				return result;

			}

			@Override
			public int getCount() {
				return suggestionsList.size();
			}

		});
		
		setupViewPager();
	}
	
	private void setupViewPager() {
		String suggestionId = (String) getIntent().getSerializableExtra(
				SuggestionFragment.SUGGESTION_ID);

		// Cycle through the Suggestions in the ArrayList to find a match

		for (int i = 0; i < suggestionsList.size(); i++) {
			if (suggestionsList.get(i).getId().equals(suggestionId)) {
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

	public void toggleIsFavourite() {
		if (currentSuggestion.getIsFavourite()) {
			currentSuggestion.setIsFavourite(false);
		} else {
			currentSuggestion.setIsFavourite(true);
		}
	}

	public void toggleIsFlagged() {
		if (currentSuggestion.getIsFlagged()) {
			currentSuggestion.setIsFlagged(false);
		} else {
			currentSuggestion.setIsFlagged(true);
		}
	}

	public void onClickAddToFavourites(View view) {
		int position = theViewPager.getCurrentItem();
		currentSuggestion = CurrentSuggestions.get(this).getSuggestionsList()
				.get(position);
		// new AddRemoveFavouriteAsync().execute();
		addRemoveFavourite();
	}

	private void addRemoveFavourite() {
		List<NameValuePair> content = new ArrayList<NameValuePair>(2);
		content.add(new BasicNameValuePair("suggestionId", currentSuggestion
				.getId()));
		content.add(new BasicNameValuePair("facebookId", User.getFacebookId()));
		String addFavouriteUrl = Constants.ADD_FAVOURITE_URL;
		String removeFavouriteUrl = Constants.FAVOURITE_URL + "remove";

		Boolean favBool = currentSuggestion.getIsFavourite();
		if (favBool) {
			HttpRequesterAsync.postJSONAsync(this, content,
					FAVOURITE_REQUEST_CODE, removeFavouriteUrl);
		} else {
			HttpRequesterAsync.postJSONAsync(this, content,
					FAVOURITE_REQUEST_CODE, addFavouriteUrl);
		}
	}

	private void updateFavouriteImage() {

		String index = Integer.toString(theViewPager.getCurrentItem());
		View currentPage = getSupportFragmentManager().findFragmentByTag(index)
				.getView();
		ImageView favouriteImageView = (ImageView) currentPage
				.findViewById(R.id.favouriteImageView);
		Drawable drawable;
		if (currentSuggestion.getIsFavourite()) {
			drawable = getResources().getDrawable(R.drawable.heart);
		} else {
			drawable = getResources().getDrawable(R.drawable.white_heart);
		}

		favouriteImageView.setImageDrawable(drawable);
	}

	private void updateFlaggedImage() {

		String index = Integer.toString(theViewPager.getCurrentItem());
		View currentPage = getSupportFragmentManager().findFragmentByTag(index)
				.getView();
		ImageView flagImageView = (ImageView) currentPage
				.findViewById(R.id.flagImageView);
		Drawable drawable;
		if (currentSuggestion.getIsFlagged()) {
			drawable = getResources().getDrawable(R.drawable.red_flag);
		} else {
			drawable = getResources().getDrawable(R.drawable.white_flag);
		}

		flagImageView.setImageDrawable(drawable);
	}

	public void onClickFlagSuggestion(View view) {
		int position = theViewPager.getCurrentItem();
		currentSuggestion = CurrentSuggestions.get(this).getSuggestionsList()
				.get(position);
		if (!currentSuggestion.getIsFlagged()) {
			flagSuggestion();
		}
	}

	private void flagSuggestion() {
		int index = theViewPager.getCurrentItem();
		currentSuggestion = suggestionsList.get(index);
		String flagUrl = Constants.SERVER_URL + "flag/"
				+ currentSuggestion.getId();
		List<NameValuePair> content = new ArrayList<NameValuePair>();
		content.add(new BasicNameValuePair("facebookId", User.getFacebookId()));
		HttpRequesterAsync.postJSONAsync(this, content, FLAG_REQUEST_CODE,
				flagUrl);
	}

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		int index = theViewPager.getCurrentItem();
		currentSuggestion = suggestionsList.get(index);
		String title = currentSuggestion.getTitle();
		String description = currentSuggestion.getDescription();
		 String imageUrl = Constants.SERVER_URL + "images/" +
		 currentSuggestion.getImage();
		//String imageUrl = "http://stringbitking.hostbg.net/wp-content/uploads/2013/03/ClassDiagram.png";
		params.putString("name", title);
		params.putString("caption", "Lets watch.");
		params.putString("description", description);
		params.putString("link", "https://developers.facebook.com/android");
		params.putString("picture", imageUrl);

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this,
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(getApplicationContext(),
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();
								addUserPoints(50);
							} else {
								// User clicked the Cancel button
								Toast.makeText(getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(getApplicationContext(),
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							// Generic, ex: network error
							Toast.makeText(getApplicationContext(),
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();
	}
	
	private void addUserPoints(int points) {
		List<NameValuePair> content = new ArrayList<NameValuePair>();
		content.add(new BasicNameValuePair("points", Integer.toString(points)));
		String url = Constants.USERS_URL + "points/" + User.getFacebookId();
		HttpRequesterAsync.postJSONAsync(this, content, url);
	}

	public void onClickShareSomething(View view) {

		publishFeedDialog();

	}

	@Override
	public void parseJSON(String json, int requestCode) {
		if (requestCode == FLAG_REQUEST_CODE) {
			toggleIsFlagged();
			updateFlaggedImage();
		}

		if (requestCode == FAVOURITE_REQUEST_CODE) {
			toggleIsFavourite();
			updateFavouriteImage();
		}
	}

}
