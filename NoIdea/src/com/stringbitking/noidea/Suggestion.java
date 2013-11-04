package com.stringbitking.noidea;

import java.util.UUID;

public class Suggestion {

	private String category;
	private String title;
	private String description;
	
	private UUID idNumber;
	
	public Suggestion() {
		
		idNumber = UUID.randomUUID();
		
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UUID getIdNumber() {
		return idNumber;
	}
	
}