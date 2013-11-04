package com.stringbitking.noidea;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentSuggestionList extends ListFragment {
	
	private ArrayList<Suggestion> suggestionsList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActivity().setTitle(R.string.fragment_suggestion_list_title);
		
		suggestionsList = CurrentSuggestions.get(getActivity()).getSuggestionsList();
		
		SuggestionsAdapter suggestionsAdapter = new SuggestionsAdapter(suggestionsList);
		
		setListAdapter(suggestionsAdapter);
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Suggestion clickedSuggestion = ((SuggestionsAdapter) getListAdapter())
				.getItem(position);

		
		Intent newIntent = new Intent(getActivity(), SuggestionViewPager.class);

		newIntent.putExtra(SuggestionFragment.SUGGESTION_ID,
				clickedSuggestion.getIdNumber());

		startActivity(newIntent);

		// END OF NEW

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		((SuggestionsAdapter) getListAdapter()).notifyDataSetChanged();

	}
	
	private class SuggestionsAdapter extends ArrayAdapter<Suggestion> {
		
		public SuggestionsAdapter(ArrayList<Suggestion> suggestions) {
			
			super(getActivity(), android.R.layout.simple_list_item_1, suggestions);
			
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

			suggestionsDescriptionTextView.setText(theSuggestion.getDescription());

			// Return the finished list item for display

			return convertView;

		}
		
	}

}