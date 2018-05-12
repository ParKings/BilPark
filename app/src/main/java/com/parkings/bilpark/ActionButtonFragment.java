package com.parkings.bilpark;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * Opens up and oversees the floating action buttons and their functionality.
 * Extends Fragment, hence is bound to its related activity, i.e. MainActivity.
 * <p>
 * Last edited by Emre Acarturk. Added JavaDocs.
 *
 * @author Furkan
 * @version 2018.05.11.0
 */
public class ActionButtonFragment extends Fragment {

	//properties
	private MainActivity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.action_button, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		activity = (MainActivity) getActivity();
		FloatingActionButton fab = view.findViewById(R.id.fab);
		fab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				activity.getUserLocation();
				activity.polygonClicked = false;
				activity.addPolygon("nanotam");
			}
		});
	}
}