package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.List;

import com.stringbitking.noidea.models.Suggestion;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class CurrentSuggestions {

	private static CurrentSuggestions currentSuggestions;

	private Context applicationContext;

	private ArrayList<Suggestion> suggestionsList;

	private CurrentSuggestions(Context applicationContext) {

		this.applicationContext = applicationContext;

		suggestionsList = new ArrayList<Suggestion>();

	}

	public static CurrentSuggestions get(Context context) {

		if (currentSuggestions == null) {

			currentSuggestions = new CurrentSuggestions(
					context.getApplicationContext());

		}

		return currentSuggestions;

	}

	public ArrayList<Suggestion> getSuggestionsList() {

		return suggestionsList;

	}
	
	public ArrayList<Suggestion> getSuggestionsImages() {

		return suggestionsList;

	}

	public Suggestion getSuggestion(String id) {

		for (Suggestion theSuggestions : suggestionsList) {

			if (theSuggestions.getId().equals(id)) {

				return theSuggestions;

			}

		}

		return null;

	}

	public void update(ArrayList<Suggestion> suggestionsListNew, List<Drawable> suggestionImages) {

		this.suggestionsList = suggestionsListNew;
		
		for(int i = 0; i < suggestionImages.size(); i++) {
			this.suggestionsList.get(i).setImageDrawable(suggestionImages.get(i));
		}
		
	}


}