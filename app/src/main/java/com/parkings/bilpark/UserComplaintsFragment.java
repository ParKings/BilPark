package com.parkings.bilpark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/*
 * Opens up the user complaints XML file and oversees
 * functionality of the related buttons and text fields.
 *
 * @author Furkan
 * @version 2018.05.12.0
 */
public class UserComplaintsFragment extends Fragment {

	//properties
	private EditText editComplaint;
	private String[] listOfLots;
	private ArrayAdapter<String> adapter;
	private LayoutInflater inflater;
	private Spinner spinner;
	private ServerUtil serverUtil;

	/**
	 * A default constructor that helps to inirtialize this fragment
	 */
	public UserComplaintsFragment() {
		serverUtil = ServerUtil.getInstance();
	}

	/*
	 * Initializes list of the names of lots while this view is created
	 *
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listOfLots = new String[]
				{ServerUtil.nanotamLotTag,
						ServerUtil.unamLotTag,
						ServerUtil.mescidLotTag};
	}

	/*
	 * Initializes UserComplaintsFragment via particular parameters
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 */
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.inflater = inflater;
		return inflater.inflate(R.layout.user_complaints_fragment, null);
	}

	/*
	 * The items in this view are initialized via this method
	 *
	 * @param view
	 * @param savedInstanceState
	 */
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		spinner = view.findViewById(R.id.names_of_lots);
		adapter = new EnhancedArrayAdapter(getActivity(), R.layout.custom_line, listOfLots, inflater);
		spinner.setAdapter(adapter);
		editComplaint = view.findViewById(R.id.editText);
		editComplaint.setText("");

		view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String complaintBody;
				String complaintLot;
				complaintBody = editComplaint.getText().toString();
				complaintLot = spinner.getSelectedItem().toString();

				if(!complaintBody.matches(""))
				{
					serverUtil.sendComplaint(complaintBody,complaintLot);
					Toast.makeText(getActivity(), "Complaint is sent to us. A user reported", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(getActivity(), "Nothing sent", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
