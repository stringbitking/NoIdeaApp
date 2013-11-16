package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.stringbitking.noidea.models.User;
import com.stringbitking.noidea.network.HttpRequesterAsync;
import com.stringbitking.noidea.network.IJSONHandler;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

public class RatingDialog extends Dialog implements IJSONHandler {
	public interface ReadyListener {
		public void ready(String name);
	}

	private static final int SEND_VOTE_REQUEST_CODE = 1;
	private static final int GET_VOTE_REQUEST_CODE = 2;
	private String suggestionId;
	private ReadyListener readyListener;
	RatingBar ratestar;

	public RatingDialog(Context context, String name,
			ReadyListener readyListener) {
		super(context);
		this.suggestionId = name;
		this.readyListener = readyListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating_dialog);
		setTitle("Glasuvai be: ");
		Button buttonOK = (Button) findViewById(R.id.saveRatingButton);
		buttonOK.setOnClickListener(new OKListener());
		ratestar = (RatingBar) findViewById(R.id.ratingbar_default);

		getVote();
	}
	
	private void getVote() {
		String url = Constants.VOTE_URL + suggestionId;
		List<NameValuePair> content = new ArrayList<NameValuePair>();
		content.add(new BasicNameValuePair("facebookId", User
				.getFacebookId()));
		HttpRequesterAsync.postJSONAsync(this, content, GET_VOTE_REQUEST_CODE, url);
	}

	private void sendVote() {
		String points = Float.toString(ratestar.getRating());
		String url = Constants.SERVER_URL + "votes/create";
		List<NameValuePair> content = new ArrayList<NameValuePair>();
		content.add(new BasicNameValuePair("suggestionId", suggestionId));
		content.add(new BasicNameValuePair("facebookId", User.getFacebookId()));
		content.add(new BasicNameValuePair("points", points));
		HttpRequesterAsync.postJSONAsync(this, content, SEND_VOTE_REQUEST_CODE, url);
	}

	private class OKListener implements android.view.View.OnClickListener {
		public void onClick(View v) {
			sendVote();
		}
	}

	private void updateRating(Float rating) {
		ratestar.setRating(rating);
	}

	@Override
	public void parseJSON(String json, int requestCode) {
		if (requestCode == SEND_VOTE_REQUEST_CODE) {
			readyListener.ready(String.valueOf(ratestar.getRating()));
			RatingDialog.this.dismiss();
		}

		if (requestCode == GET_VOTE_REQUEST_CODE) {
			try {
				JSONObject response = new JSONObject(json);
				String points = response.getString("points");
				updateRating(Float.parseFloat(points));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
