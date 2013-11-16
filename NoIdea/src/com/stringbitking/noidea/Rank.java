package com.stringbitking.noidea;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Rank {
	private static int[] pointLevels = { 0, 10, 50, 100 };
	
	public static int getLevel(int points) {
		for(int i = 0; i < pointLevels.length - 1; i++) {
			if(points < pointLevels[i+1]) {
				return i;
			}
		}
		
		return pointLevels.length - 1;
	}
	
	public static int getNextRankPoints(int points) {
		int level = getLevel(points);
		int nextRankPoints = 100000;
		if(level < pointLevels.length - 1) {
			nextRankPoints = pointLevels[level + 1];
		}
		
		return nextRankPoints;
	}
	
	public static Drawable getDrawable(Context ctx, int points) {
		Drawable drawable = ctx.getResources().getDrawable(R.drawable.applicant);
		
		int level = getLevel(points);
		
		switch(level) {
		case 0:
			drawable = ctx.getResources().getDrawable(R.drawable.applicant);
			break;
		case 1:
			drawable = ctx.getResources().getDrawable(R.drawable.advisor);
			break;
		case 2:
			drawable = ctx.getResources().getDrawable(R.drawable.recruiter);
			break;
		case 3:
			drawable = ctx.getResources().getDrawable(R.drawable.webmaster);
			break;
		}
		
		return drawable;
	}
	
	public static String getRankName(int points) {
		String rankName = "Baby Suggester";
		
		int level = getLevel(points);
		
		switch(level) {
		case 0:
			rankName = "Applicant";
			break;
		case 1:
			rankName = "Advisor";
			break;
		case 2:
			rankName = "Recruiter";
			break;
		case 3:
			rankName = "Master";
			break;
		}
		
		return rankName;
	}
	
}
