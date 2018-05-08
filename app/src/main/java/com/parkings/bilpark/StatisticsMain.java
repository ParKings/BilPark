package com.parkings.bilpark;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.uur.bilpark.R;

/**
 * StatisticsFragment class
 * which extends Fragment class to provide UI design
 * for the statistics option of the main menu
 *
 * Created by uğur on 27.03.2018.
 * Modified by furkan on 05.05.2018
 *
 * @author furkan
 * @version 2018.05.05.0
 */

// IN FRAGMENTS USE getActivity() METHOD INSTEAD OF getApplicationContext() THAT YOU WOULD NORMALLY USE
public class StatisticsMain extends Fragment {

	/**
	 * Initializes a View related to this fragment
	 *
	 * @param inflater
	 * @param group
	 * @param savedInstanceState
	 * @return View
	 *
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		Fragment fragment = new StatisticsFragment(/*lots[index]*/);
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.statistics, fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		return inflater.inflate(R.layout.statistics_main, null);
	}
}
