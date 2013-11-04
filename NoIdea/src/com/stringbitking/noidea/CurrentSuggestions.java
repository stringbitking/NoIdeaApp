package com.stringbitking.noidea;

import java.util.ArrayList;

import android.content.Context;

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

	public Suggestion getSuggestion(String id) {

		for (Suggestion theSuggestions : suggestionsList) {

			if (theSuggestions.getId().equals(id)) {

				return theSuggestions;

			}

		}

		return null;

	}

	public void update(ArrayList<Suggestion> suggestionsListNew) {

		this.suggestionsList = suggestionsListNew;
		
	}


}