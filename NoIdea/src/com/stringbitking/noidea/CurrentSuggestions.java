package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class CurrentSuggestions {

	private static CurrentSuggestions currentSuggestions;

	private Context applicationContext;

	private ArrayList<Suggestion> suggestionsList;

	private CurrentSuggestions(Context applicationContext) {

		this.applicationContext = applicationContext;

		suggestionsList = new ArrayList<Suggestion>();

		Suggestion movie1 = new Suggestion();
		movie1.setCategory("Movies");
		movie1.setTitle("Matrix");
		movie1.setDescription("Mr. Anderson vs the world!");
		suggestionsList.add(movie1);

		Suggestion movie2 = new Suggestion();
		movie2.setCategory("Movies");
		movie2.setTitle("Thor");
		movie2.setDescription("The god Thor has come to Earth.");
		suggestionsList.add(movie2);

		Suggestion movie3 = new Suggestion();
		movie3.setCategory("Movies");
		movie3.setTitle("Iron Man");
		movie3.setDescription("Mr. Tony Stark in yet another thrilling movie from the series.");
		suggestionsList.add(movie3);

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

	public Suggestion getSuggestion(UUID id) {

		for (Suggestion theSuggestions : suggestionsList) {

			if (theSuggestions.getIdNumber().equals(id)) {

				return theSuggestions;

			}

		}

		return null;

	}

}