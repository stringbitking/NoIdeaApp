package com.stringbitking.noidea;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class PostSuggestionActivity extends Activity {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;

	private static String createSuggestionUrl = Constants.CREATE_SUGGESTION_URL;
	private Boolean isImageReady = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_suggestion);

		loadCategories();

	}

	private void loadCategories() {

		Spinner spinner = (Spinner) findViewById(R.id.categoriesSpinner);

		CategoriesAdapter spinnerAdapter = new CategoriesAdapter(this,
				CategoriesProvider.get().getCategoriesList(), false);

		spinner.setAdapter(spinnerAdapter);

	}

	public void onClickCreateSuggestion(View view) {

		EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
		EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		Spinner categoriesSpinner = (Spinner) findViewById(R.id.categoriesSpinner);

		String title = titleEditText.getText().toString();
		String description = descriptionEditText.getText().toString();
		String categoryId = ((Category) categoriesSpinner.getSelectedItem())
				.getId();

		if (title.length() > 0 && description.length() > 0 && isImageReady) {

			new CreateSuggestion().execute(title, description, categoryId);

		} else {

			Toast.makeText(getBaseContext(), "All fields are required.",
					Toast.LENGTH_SHORT).show();

		}

	}

	public void onClickChangePicture(View view) {

		// create Intent to take a picture and return control to the calling
		// application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to
															// save the image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
															// name

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				isImageReady = true;
				ImageView imgView = (ImageView) findViewById(R.id.suggestionImageView);
				imgView.setImageURI(fileUri);

				Toast.makeText(this, "Image saved to:\n" + fileUri,
						Toast.LENGTH_LONG).show();

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	private void clearSuggestionForm() {

		EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
		EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		Spinner categoriesSpinner = (Spinner) findViewById(R.id.categoriesSpinner);

		titleEditText.setText("");
		descriptionEditText.setText("");
		categoriesSpinner.setSelection(0);

		Toast.makeText(getBaseContext(), "Suggestion created.",
				Toast.LENGTH_SHORT).show();

	}

	private class CreateSuggestion extends AsyncTask<String, String, String> {

		protected String doInBackground(String... params) {

			String title = params[0];
			String description = params[1];
			String categoryId = params[2];
			
			File suggestionImage = new File(fileUri.getPath());
			FileBody imgBody = new FileBody(suggestionImage);

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(createSuggestionUrl);

			try {

				MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder
						.create();
				
				multipartBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				
				multipartBuilder.addTextBody("title", title);
				multipartBuilder.addTextBody("description", description);
				multipartBuilder.addTextBody("categoryId", categoryId);
				multipartBuilder.addPart("image", imgBody);
				
				httppost.setEntity(multipartBuilder.build());

				httpclient.execute(httppost);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return "";

		}

		protected void onPostExecute(String result) {

			clearSuggestionForm();

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_suggestion, menu);
		return true;
	}

}
