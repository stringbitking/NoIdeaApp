package com.stringbitking.noidea;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.stringbitking.noidea.models.User;
import com.stringbitking.noidea.network.HttpRequesterAsync;
import com.stringbitking.noidea.network.IJSONHandler;

public class SelectionFragment extends Fragment implements IJSONHandler {

	private static final int REAUTH_ACTIVITY_CODE = 100;

	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private TextView pointsTextView;
	private ImageView rankImageView;
	private TextView pointsRankTextView;
	private TextView rankNameTextView;

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state,
				final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.selection, container, false);

		pointsTextView = (TextView) view.findViewById(R.id.pointsTextView);
		// Find the user's profile picture custom view
		profilePictureView = (ProfilePictureView) view
				.findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);

		pointsRankTextView = (TextView) view
				.findViewById(R.id.pointsRankTextView);
		rankImageView = (ImageView) view.findViewById(R.id.rankImageView);
		rankNameTextView = (TextView) view.findViewById(R.id.rankNameTextView);

		// Find the user's name view
		userNameView = (TextView) view.findViewById(R.id.selection_user_name);

		// Check for an open session
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Get the user's data
			makeMeRequest(session);
		} else {

			profilePictureView.setVisibility(View.INVISIBLE);
			userNameView.setVisibility(View.INVISIBLE);

		}

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {

								String userId = user.getId();
								String userName = user.getName();

								User.setFacebookId(userId);
								User.setName(userName);
								User.setIsUserLoggedIn(true);

								// Set the id for the ProfilePictureView
								// view that in turn displays the profile
								// picture.
								profilePictureView.setProfileId(User
										.getFacebookId());
								// Set the Textview's text to the user's name.
								userNameView.setText(User.getName());

								getUserInformation();
							}
						}
						if (response.getError() != null) {
							// Handle errors, will do so later.
						}
					}
				});

		request.executeAsync();
	}

	private void getUserInformation() {
		List<NameValuePair> content = new ArrayList<NameValuePair>(1);
		String name = User.getName();
		content.add(new BasicNameValuePair("name", name));
		String getUserUrl = Constants.USERS_URL + User.getFacebookId();
		HttpRequesterAsync.postJSONAsync(this, content, getUserUrl);
	}

	private void onSessionStateChange(final Session session,
			SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);

			profilePictureView.setVisibility(View.VISIBLE);
			userNameView.setVisibility(View.VISIBLE);

		} else {

			profilePictureView.setVisibility(View.INVISIBLE);
			userNameView.setVisibility(View.INVISIBLE);

		}
	}

	private void updateLayout() {
		int points = User.getPoints();
		String pointsStr = Integer.toString(points);
		pointsTextView.setText(pointsStr);

		Drawable rankDrawable = Rank.getDrawable(getActivity(), points);
		rankImageView.setImageDrawable(rankDrawable);

		int nextRankPoints = Rank.getNextRankPoints(points);
		pointsRankTextView.setText(Integer.toString(nextRankPoints));

		String rankName = Rank.getRankName(points);
		rankNameTextView.setText(rankName);
	}

	@Override
	public void parseJSON(String json, int requestCode) {
		// parse user information
		try {
			JSONObject responseBody = new JSONObject(json);
			User.setId(responseBody.getString("_id"));
			User.setPoints(responseBody.getInt("points"));
			updateLayout();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}