package com.stringbitking.noidea;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.stringbitking.noidea.models.User;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

public class RatingDialog extends Dialog {
	public interface ReadyListener {
		public void ready(String name);
	}

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
		
		new GetVoteAsync().execute();
	}

	private class OKListener implements android.view.View.OnClickListener {
		public void onClick(View v) {
			new SendVoteAsync().execute();
			
		}
	}
	
	private void updateRating(Float rating) {
		ratestar.setRating(rating);
	}
	
	private class SendVoteAsync extends AsyncTask<String, String, String> {

		protected String doInBackground(String... arg0) {

			String points = ratestar.getRating() + "";
			String url = Constants.SERVER_URL + "votes/create";
			List<NameValuePair> content = new ArrayList<NameValuePair>();
			content.add(new BasicNameValuePair("suggestionId", suggestionId));
			content.add(new BasicNameValuePair("facebookId", User.getFacebookId()));
			content.add(new BasicNameValuePair("points", points));
			String result = HttpRequester.PostJSON(url, content);

			return result;

		}

		protected void onPostExecute(String result) {

			readyListener.ready(String.valueOf(ratestar.getRating()));
			RatingDialog.this.dismiss();

		}

	}
	
	private class GetVoteAsync extends AsyncTask<String, String, String> {

		protected String doInBackground(String... arg0) {

			String url = Constants.VOTE_URL + suggestionId;
			List<NameValuePair> content = new ArrayList<NameValuePair>();
			content.add(new BasicNameValuePair("facebookId", User.getFacebookId()));
			String result = HttpRequester.PostJSON(url, content);

			return result;

		}

		protected void onPostExecute(String result) {

			try {
				JSONObject response = new JSONObject(result);
				String points = response.getString("points");
				updateRating(Float.parseFloat(points));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
}
