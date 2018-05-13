package com.parkings.bilpark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A modified ArrayAdapter which is used to define the options in the spinners used in
 * DetailedStatisticsFragment and UserComplaintsFragment
 *
 * @author furkan
 * @version 2018.05.12.0
 */
public class EnhancedArrayAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;

	/*
	 * Initializes EnhancedArrayAdapter via particular parameters
	 *
	 * @param context
	 * @param resourceId
	 * @param list
	 * @param inflater
	 */
	public EnhancedArrayAdapter(Context context, int resourceId, String[] list, LayoutInflater inflater) {
		super(context, resourceId, list);
		this.inflater = inflater;
	}

	/*
	 * Defines the options in the list displayed via spinners
	 *
	 * @param index
	 * @param view
	 * @param container
	 * @return View
	 */
	@Override
	public View getDropDownView(int index, View view, ViewGroup container) {
		return getCustomView(index);
	}

	/*
	 * Intializes the selected option in the list
	 *
	 * @param index
	 * @param view
	 * @param container
	 * @return View
	 */
	@Override
	public View getView(int index, View view, ViewGroup container) {
		return getCustomView(index);
	}

	/*
	 * Intializes each option in the list one by one
	 *
	 * @param index
	 * @return View
	 */
	public View getCustomView(int index) {
		View view = inflater.inflate(R.layout.custom_line, null);
		TextView text = view.findViewById(R.id.anOption);
		text.setText(getItem(index));

		return view;
	}
}