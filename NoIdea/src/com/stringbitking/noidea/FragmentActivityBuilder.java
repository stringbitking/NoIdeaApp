package com.stringbitking.noidea;

import com.stringbitking.noidea.actionbar.ActionBarFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public abstract class FragmentActivityBuilder extends ActionBarFragmentActivity {

	protected abstract Fragment createFragment();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.activity_suggestions_list);
		FragmentManager fragManager = getSupportFragmentManager();

		Fragment theFragment = fragManager
				.findFragmentById(R.id.fragmentContainer);

		if (theFragment == null) {

			// If the Fragment wasn't found then create it
			theFragment = createFragment();

			fragManager.beginTransaction()
					.add(R.id.fragmentContainer, theFragment).commit();

		}

	}

}