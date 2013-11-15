package com.stringbitking.noidea.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.stringbitking.noidea.models.Category;

public class JSONParser {
	public static List<Category> parseCategories(String categoriesJSON) {
		List<Category> categories = new ArrayList<Category>();

		try {
			JSONArray arrResult = new JSONArray(categoriesJSON);

			for (int i = 0; i < arrResult.length(); i++) {
				JSONObject categoryJSON = arrResult.getJSONObject(i);

				Category cat = new Category();
				cat.setId(categoryJSON.getString("_id"));
				cat.setName(categoryJSON.getString("name"));
				cat.setVerb(categoryJSON.getString("verb"));
				categories.add(cat);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return categories;
	}
}
