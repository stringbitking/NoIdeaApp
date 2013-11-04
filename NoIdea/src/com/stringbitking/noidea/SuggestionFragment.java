package com.stringbitking.noidea;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SuggestionFragment extends Fragment {
	
	public static final String SUGGESTION_ID = "com.example.pagersuggestions.suggestion_id";
	
	private Suggestion suggestion;
	
	private TextView fragmentSuggestionTitleTextView;
	private TextView fragmentSuggestionDescriptionTextView;
	
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

		String suggestionId = (String) getArguments().getSerializable(SUGGESTION_ID);

		// Get the Contact with the matching ID

		suggestion = CurrentSuggestions.get(getActivity()).getSuggestion(suggestionId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View theView = inflater.inflate(R.layout.fragment_suggestion, container, false);
		
		fragmentSuggestionTitleTextView = (TextView) theView.findViewById(R.id.fragmentSuggestionTitleTextView);
		fragmentSuggestionDescriptionTextView  = (TextView) theView.findViewById(R.id.fragmentSuggestionDescriptionTextView);
		
		fragmentSuggestionTitleTextView.setText(suggestion.getTitle());
		fragmentSuggestionDescriptionTextView.setText(suggestion.getDescription());
		
		return theView;
		
	}
	
	

}

