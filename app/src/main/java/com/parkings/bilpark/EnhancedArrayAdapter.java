package com.parkings.bilpark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EnhancedArrayAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;

	public EnhancedArrayAdapter(Context context, int resourceId, String[] list, LayoutInflater inflater) {
		super(context, resourceId, list);
		this.inflater = inflater;
	}

	@Override
	public View getDropDownView(int index, View view, ViewGroup container) {
		return getCustomView(index);
	}

	@Override
	public View getView(int index, View view, ViewGroup container) {
		return getCustomView(index);
	}

	public View getCustomView(int index) {
		View view = inflater.inflate(R.layout.custom_line, null);
		TextView text = view.findViewById(R.id.anOption);
		text.setText(getItem(index));

		return view;
	}
}