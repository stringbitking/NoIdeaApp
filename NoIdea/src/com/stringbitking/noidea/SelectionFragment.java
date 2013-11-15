package com.stringbitking.noidea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.stringbitking.noidea.models.Suggestion;
import com.stringbitking.noidea.models.User;
import com.stringbitking.noidea.network.HttpRequester;

public class SelectionFragment extends Fragment {

	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final String TAG = "SelectionFragment";

	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private TextView pointsTextView;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
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

		// Find the user's name view
		userNameView = (TextView) view.findViewById(R.id.selection_user_name);
		
		// Check for an open session
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        makeMeRequest(session);
	    }
	    else {
	    	
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
	                	String userName =  user.getName();
	                	
	                	User.setFacebookId(userId);
	                	User.setName(userName);
	                	User.setIsUserLoggedIn(true);
	                	
	                    // Set the id for the ProfilePictureView
	                    // view that in turn displays the profile picture.
	                    profilePictureView.setProfileId(User.getFacebookId());
	                    // Set the Textview's text to the user's name.
	                    userNameView.setText(User.getName());
	                    
	                    new GetUserInformation().execute();
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    
	    request.executeAsync();
	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);
	        
	        profilePictureView.setVisibility(View.VISIBLE);
	    	userNameView.setVisibility(View.VISIBLE);
	    	
	    }
	    else {
	    	
	    	profilePictureView.setVisibility(View.INVISIBLE);
	    	userNameView.setVisibility(View.INVISIBLE);
	    	
	    }
	}
	
	private void updateLayout() {
		int points = User.getPoints();
		String pointsStr = Integer.toString(points);
		pointsTextView.setText(pointsStr);
	}

	private class GetUserInformation extends AsyncTask<String, String, String> {

		protected String doInBackground(String... arg0) {

			List<NameValuePair> content = new ArrayList<NameValuePair>(1);
			content.add(new BasicNameValuePair("name", User.getName()));
			String getUserUrl = Constants.USERS_URL + User.getFacebookId();
			
			String result = HttpRequester.PostJSON(getUserUrl, content);
			return result;

		}

		protected void onPostExecute(String result) {

			try {

				JSONObject responseBody = new JSONObject(result);
				User.setId(responseBody.getString("_id"));
				User.setPoints(responseBody.getInt("points"));
				updateLayout();
			} catch (JSONException e) {

				e.printStackTrace();

			}

		}

		

	}
	
}