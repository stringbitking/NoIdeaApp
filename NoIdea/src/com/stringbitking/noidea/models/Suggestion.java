package com.stringbitking.noidea.models;

import android.graphics.drawable.Drawable;

public class Suggestion {

	private String id;
	private String category;
	private String title;
	private String description;
	private String image;
	private Drawable imageDrawable;
	private String author;
	private String authorId;
	private Boolean isFavourite;
	private Boolean isFlagged;
	
	public Boolean getIsFlagged() {
		return isFlagged;
	}

	public void setIsFlagged(Boolean isFlagged) {
		this.isFlagged = isFlagged;
	}

	public Boolean getIsFavourite() {
		return isFavourite;
	}

	public void setIsFavourite(Boolean isFavourite) {
		this.isFavourite = isFavourite;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public Drawable getImageDrawable() {
		return imageDrawable;
	}

	public void setImageDrawable(Drawable imageDrawable) {
		this.imageDrawable = imageDrawable;
	}

	public Suggestion() {
		
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	
	
}