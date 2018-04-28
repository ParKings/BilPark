package com.parkings.bilpark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uur.bilpark.R;

/**
 * Created by uğur on 27.03.2018.
 */

// IN FRAGMENTS USE getActivity() METHOD INSTEAD OF getApplicationContext() THAT YOU WOULD NORMALLY USE
public class StatisticsFragment extends Fragment {

	/* __________STUB__________ */
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.statistics_fragment, null);
	}
}
