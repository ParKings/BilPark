package com.parkings.bilpark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class AppComplaintsFragment extends Fragment {

	private EditText editComplaint;
	private ServerUtil serverUtil;

	public AppComplaintsFragment() {
		serverUtil = ServerUtil.getInstance();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.app_complaints_fragment, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		editComplaint = view.findViewById(R.id.editText);
		editComplaint.setText("");

		view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String complaintBody;
				complaintBody = editComplaint.getText().toString();

				if(!complaintBody.matches(""))
				{
					serverUtil.sendComplaint(complaintBody);
					Toast.makeText(getActivity(), "Thanks for the feedback", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(getActivity(), "Nothing sent", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
