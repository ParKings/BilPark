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


public class UserComplaintsFragment extends Fragment {

	private EditText editComplaint;
	private String[] listOfLots;
	private ArrayAdapter<String> adapter;
	private LayoutInflater inflater;
	private Spinner spinner;
	private ServerUtil serverUtil;

	public UserComplaintsFragment() {
		serverUtil = ServerUtil.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listOfLots = new String[]
				{ServerUtil.nanotamLotTag,
						ServerUtil.unamLotTag,
						ServerUtil.mescidLotTag};
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.inflater = inflater;
		return inflater.inflate(R.layout.user_complaints_fragment, null);
	}

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
