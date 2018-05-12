package com.parkings.bilpark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A class that helps to express our thoughts and intents
 * related to this project
 *
 * Created by uğur on 27.03.2018. Opens up the related XML file.
 * Modified by furkan on 12.05.2018
 *
 * @author Ugur
 * @version 2018.05.11.0
 */
public class AboutUsFragment extends Fragment {

	/*
	 * Initializes aboutUsFragment via particular parameters
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 */
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.about_us_fragment, null);
		TextView text = view.findViewById(R.id.info);
		text.setText("We are a group of developers from Bilkent University" +
				"\n\nGroup Members are:\n\tEmre Acarturk\n\tAhmet Furkan Turgut" +
				"\n\tAltan Akat\n\tUgur Yilmaz\n\tYaman Yucel" +
				"\n\nWe hope that you liked our first app\n" +
				"\nDo not forget to give feedback from Complaints");
		return view;
	}
}
