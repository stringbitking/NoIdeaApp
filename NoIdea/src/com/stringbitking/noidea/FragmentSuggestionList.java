package com.stringbitking.noidea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.stringbitking.noidea.models.Suggestion;
import com.stringbitking.noidea.models.User;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentSuggestionList extends ListFragment {

	private String option;
	private List<Suggestion> suggestionsList;
	private List<Drawable> suggestionImages;

	public static String DISPLAY_OPTION = "com.stringbitking.noidea.display";
	public static String SINGLE_CATEGORY_OPTION = "single";
	public static String FAVOURITES_OPTION = "favourites";

	private static String suggestionsUrl = Constants.SUGGESTIONS_URL;
	private String categoryId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActivity().setTitle(R.string.fragment_suggestion_list_title);

		categoryId = (String) getActivity().getIntent().getSerializableExtra(
				MainActivity.CATEGORY_ID);
		option = getArguments().getString(DISPLAY_OPTION);
		
		new GetSuggestionsJSON().execute(option);

		// Setting up the adapter after executing the async operation

	}

	private void setAdapter() {

		suggestionsList = CurrentSuggestions.get(getActivity())
				.getSuggestionsList();

		SuggestionsAdapter suggestionsAdapter = new SuggestionsAdapter(
				(ArrayList<Suggestion>) suggestionsList);

		setListAdapter(suggestionsAdapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Suggestion clickedSuggestion = ((SuggestionsAdapter) getListAdapter())
				.getItem(position);

		Intent newIntent = new Intent(getActivity(), SuggestionViewPager.class);

		newIntent.putExtra(SuggestionFragment.SUGGESTION_ID,
				clickedSuggestion.getId());

		startActivity(newIntent);

		// END OF NEW

	}

	private class SuggestionsAdapter extends ArrayAdapter<Suggestion> {

		public SuggestionsAdapter(ArrayList<Suggestion> suggestions) {

			super(getActivity(), android.R.layout.simple_list_item_1,
					suggestions);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// Check if this is a recycled list item and if not we inflate it

			if (convertView == null) {

				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_suggestion, null);

			}

			// Find the right data to put in the list item

			Suggestion theSuggestion = getItem(position);

			// Put the right data into the right components

			TextView suggestionTitleTextView = (TextView) convertView
					.findViewById(R.id.suggestionTitleTextView);

			suggestionTitleTextView.setText(theSuggestion.getTitle());

			TextView suggestionsDescriptionTextView = (TextView) convertView
					.findViewById(R.id.suggestionDescriptionTextView);

			suggestionsDescriptionTextView.setText(theSuggestion
					.getDescription());

			ImageView suggestionImageView = (ImageView) convertView
					.findViewById(R.id.suggestionImageView);

			suggestionImageView
					.setImageDrawable(suggestionImages.get(position));

			// Return the finished list item for display

			return convertView;

		}

	}

	public static Drawable LoadImageFromWebOperations(String url) {

		try {

			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;

		} catch (Exception e) {

			return null;

		}

	}

	private class LoadSuggestionImages extends
			AsyncTask<String, String, String> {

		protected String doInBackground(String... params) {

			suggestionImages = new ArrayList<Drawable>();
			String imagesUrl = "http://10.0.3.2:3000/images/";

			for (int i = 0; i < suggestionsList.size(); i++) {

				String currentImgUrl = imagesUrl
						+ suggestionsList.get(i).getImage();
				Drawable drawableImage = LoadImageFromWebOperations(currentImgUrl);
				suggestionImages.add(drawableImage);

			}

			return "";

		}

		protected void onPostExecute(String result) {

			CurrentSuggestions cg = CurrentSuggestions.get(getActivity());
			cg.update((ArrayList<Suggestion>) suggestionsList, suggestionImages);

			setAdapter();

		}

	}

	private class GetSuggestionsJSON extends AsyncTask<String, String, String> {

		protected String doInBackground(String... params) {

			String option = params[0];
			String result = "";
			if (option == SINGLE_CATEGORY_OPTION) {
				result = HttpRequester.GetJSON(suggestionsUrl
						+ categoryId);
			}
			else {
				String favUrl = Constants.FAVOURITE_URL + User.getId();
				result = HttpRequester.GetJSON(favUrl);
			}

			return result;

		}

		protected void onPostExecute(String result) {

			try {

				suggestionsList = new ArrayList<Suggestion>();
				JSONArray arrResult = new JSONArray(result);

				for (int i = 0; i < arrResult.length(); i++) {

					JSONObject suggestionJSON = arrResult.getJSONObject(i);

					Suggestion currentSuggestion = new Suggestion();
					currentSuggestion.setId(suggestionJSON.getString("_id"));
					currentSuggestion.setCategory(suggestionJSON
							.getString("_category"));
					currentSuggestion.setDescription(suggestionJSON
							.getString("description"));
					currentSuggestion.setTitle(suggestionJSON
							.getString("title"));
					currentSuggestion.setImage(suggestionJSON
							.getString("image"));
					suggestionsList.add(currentSuggestion);

				}

			} catch (JSONException e) {

				e.printStackTrace();

			}

			new LoadSuggestionImages().execute();

		}

	}

}