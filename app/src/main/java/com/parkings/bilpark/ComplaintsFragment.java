package com.parkings.bilpark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by uğur on 27.03.2018.
 */
public class ComplaintsFragment extends Fragment {

	private RadioGroup radioComplaintGroup;
	private RadioButton radiobutton1;
	private RadioButton radiobutton2;
	private Fragment fragment;

	/* __________STUB__________ */
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		fragment = null;
		return inflater.inflate(R.layout.complaints_fragment, null);
	}

	/* __________STUB__________ */
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		radioComplaintGroup = view.findViewById(R.id.radioGroup);
		radiobutton1 = view.findViewById(R.id.radioButton1);
		radiobutton2 = view.findViewById(R.id.radioButton2);

		radiobutton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment = new UserComplaintsFragment();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.complaint_type, fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});

		radiobutton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment = new AppComplaintsFragment();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.complaint_type, fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});
	}
}
