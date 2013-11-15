package com.stringbitking.noidea.models;

public class User {
	
	private static String id;
	private static String facebookId;
	private static String name;
	private static int points;
	private static Boolean isUserLoggedIn = false;
	
	public static int getPoints() {
		return points;
	}
	public static void setPoints(int points) {
		User.points = points;
	}
	public static String getId() {
		return id;
	}
	public static void setId(String id) {
		User.id = id;
	}
	public static String getName() {
		return name;
	}
	public static void setName(String name) {
		User.name = name;
	}
	public static Boolean getIsUserLoggedIn() {
		return isUserLoggedIn;
	}
	public static void setIsUserLoggedIn(Boolean isUserLoggedIn) {
		User.isUserLoggedIn = isUserLoggedIn;
	}
	public static String getFacebookId() {
		return facebookId;
	}
	public static void setFacebookId(String facebookId) {
		User.facebookId = facebookId;
	}
	
}
