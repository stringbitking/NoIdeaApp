package com.stringbitking.noidea;

import java.util.List;

import com.stringbitking.noidea.models.Category;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CategoriesAdapter implements SpinnerAdapter {

	private List<Category> data;
	private Activity context;
	private Boolean showVerb;

	public CategoriesAdapter(Activity context, List<Category> categories,
			Boolean showVerb) {

		this.context = context;
		this.data = categories;
		this.showVerb = showVerb;

	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 1;
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView) View.inflate(context,
				android.R.layout.simple_spinner_item, null);
		String textToShow;

		if (this.showVerb) {

			textToShow = data.get(position).getVerb();

		} else {

			textToShow = data.get(position).getName();

		}

		textView.setText(textToShow);
		return textView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {

		if (data != null && data.size() > 0) {
			return false;
		}

		return true;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(
					android.R.layout.simple_spinner_dropdown_item, null);
		}

		String textToShow;

		if (this.showVerb) {

			textToShow = data.get(position).getVerb();

		} else {

			textToShow = data.get(position).getName();

		}

		((TextView) convertView).setText(textToShow);
		return convertView;
	}

}