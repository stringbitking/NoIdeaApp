package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.List;

import com.stringbitking.noidea.models.Category;

public class CategoriesProvider {
	
	private static CategoriesProvider categoriesProvider;
	
	private List<Category> categoriesList;
	
	private CategoriesProvider() {
		
		categoriesList = new ArrayList<Category>();

	}
	
	public static CategoriesProvider get() {

		if (categoriesProvider == null) {

			categoriesProvider = new CategoriesProvider();

		}

		return categoriesProvider;

	}
	
	public List<Category> getCategoriesList() {
		
		return this.categoriesList;
		
	}

	public void update(List<Category> newCategories) {
		
		this.categoriesList = newCategories;
		
	}
	
}
