package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.stringbitking.noidea.models.Suggestion;
import com.stringbitking.noidea.models.User;
import com.stringbitking.noidea.network.HttpRequesterAsync;
import com.stringbitking.noidea.network.IJSONHandler;

public class SuggestionFragment extends Fragment implements IJSONHandler {

	public static final String SUGGESTION_ID = "com.example.pagersuggestions.suggestion_id";
	private static final int RATING_REQUEST_CODE = 1;
	private static final int FAVOURITE_REQUEST_CODE = 2;
	private Suggestion suggestion;
	public Boolean isFavourite;
	public Boolean isFlagged;

	private TextView fragmentSuggestionTitleTextView;
	private TextView fragmentSuggestionDescriptionTextView;
	private ImageView suggestionImageView;
	private ImageView flagImageView;
	private RatingBar indicatorRatingBar;
	private Button rateButton;
	private ImageView favouriteImageView;

	public static SuggestionFragment newSuggestionFragment(String suggestionId) {

		// A Bundle is used to pass data between Activities

		Bundle passedData = new Bundle();

		// Put the Suggestion ID in the Bundle

		passedData.putSerializable(SUGGESTION_ID, suggestionId);

		SuggestionFragment suggestionFragment = new SuggestionFragment();

		suggestionFragment.setArguments(passedData);

		return suggestionFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String suggestionId = (String) getArguments().getSerializable(
				SUGGESTION_ID);

		// Get the Contact with the matching ID

		suggestion = CurrentSuggestions.get(getActivity()).getSuggestion(
				suggestionId);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View theView = inflater.inflate(R.layout.fragment_suggestion,
				container, false);

		fragmentSuggestionTitleTextView = (TextView) theView
				.findViewById(R.id.fragmentSuggestionTitleTextView);
		fragmentSuggestionDescriptionTextView = (TextView) theView
				.findViewById(R.id.fragmentSuggestionDescriptionTextView);
		suggestionImageView = (ImageView) theView
				.findViewById(R.id.suggestionImageView);
		flagImageView = (ImageView) theView.findViewById(R.id.flagImageView);
		indicatorRatingBar = (RatingBar) theView
				.findViewById(R.id.indicatorRatingBar);
		rateButton = (Button) theView.findViewById(R.id.rateButton);
		favouriteImageView = (ImageView) theView
				.findViewById(R.id.favouriteImageView);

		fragmentSuggestionTitleTextView.setText(suggestion.getTitle());
		fragmentSuggestionDescriptionTextView.setText(suggestion
				.getDescription());
		suggestionImageView.setImageDrawable(suggestion.getImageDrawable());
		rateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				RatingDialog myDialog = new RatingDialog(getActivity(),
						suggestion.getId(), new OnReadyListener());
				myDialog.show();
			}
		});

		// new CheckFavouriteAsync().execute();
		checkFavourite();
		calculateRating();

		return theView;

	}

	private void checkFavourite() {
		List<NameValuePair> content = new ArrayList<NameValuePair>();
		content.add(new BasicNameValuePair("facebookId", User.getFacebookId()));
		content.add(new BasicNameValuePair("suggestionId", suggestion.getId()));
		String url = Constants.FAVOURITE_URL + "check";
		HttpRequesterAsync.postJSONAsync(this, content, FAVOURITE_REQUEST_CODE, url);
	}

	public void calculateRating() {
		String ratingUrl = Constants.RATING_URL + suggestion.getId();
		HttpRequesterAsync.getJSONAsync(this, RATING_REQUEST_CODE, ratingUrl);
	}

	private class OnReadyListener implements RatingDialog.ReadyListener {

		public void ready(String name) {
			calculateRating();
		}
	}

	private void updateRating(Float newRating) {
		indicatorRatingBar.setRating(newRating);
	}

	private void updateFavouriteImage() {

		Drawable drawable;
		if (isFavourite) {
			drawable = getResources().getDrawable(R.drawable.heart);
		} else {
			drawable = getResources().getDrawable(R.drawable.white_heart);
		}

		favouriteImageView.setImageDrawable(drawable);

		if (isFlagged) {
			drawable = getResources().getDrawable(R.drawable.red_flag);
		} else {
			drawable = getResources().getDrawable(R.drawable.white_flag);
		}

		flagImageView.setImageDrawable(drawable);
	}

	@Override
	public void parseJSON(String json, int requestCode) {
		if (requestCode == RATING_REQUEST_CODE) {
			try {
				JSONObject response = new JSONObject(json);
				String ratingStr = response.getString("rating");
				Float ratingNum = Float.parseFloat(ratingStr);
				updateRating(ratingNum);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (requestCode == FAVOURITE_REQUEST_CODE) {
			try {
				JSONObject response = new JSONObject(json);
				String isFavouriteStr = response.getString("isFavourite");
				String isFlaggedStr = response.getString("isFlagged");
				isFavourite = false;
				isFlagged = false;
				if (isFavouriteStr.equalsIgnoreCase("true")) {
					isFavourite = true;
				}
				if (isFlaggedStr.equalsIgnoreCase("true")) {
					isFlagged = true;
				}
				suggestion.setIsFavourite(isFavourite);
				suggestion.setIsFlagged(isFlagged);
				updateFavouriteImage();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
