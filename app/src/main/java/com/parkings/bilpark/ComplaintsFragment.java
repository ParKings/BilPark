package com.parkings.bilpark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.uur.bilpark.R;

/**
 * Created by uğur on 27.03.2018.
 */

public class ComplaintsFragment extends Fragment {

	private RadioGroup radioComplaintGroup;
	private RadioButton radiobutton1;
	private RadioButton radiobutton2;
	private EditText editComplaint;
	private EditText editLot;


	/* __________STUB__________ */
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.complaints_fragment, null);
	}

	/* __________STUB__________ */
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Complaint complaint = new Complaint();
		radioComplaintGroup = view.findViewById(R.id.radioGroup);
		radiobutton1 = view.findViewById(R.id.radioButton);
		radiobutton2 = view.findViewById(R.id.radioButton2);
		editComplaint = view.findViewById(R.id.editText);
		editLot = view.findViewById(R.id.editText2);
		radiobutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        radiobutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

		editComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editComplaint.setText("");
            }
        });
        editLot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLot.setText("");
            }
        });

		view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			    String complaintBody;
			    String complaintLot;
			    complaintBody = editComplaint.getText().toString();
                complaintLot = editLot.getText().toString();
			    if(radiobutton1.isChecked() && complaintBody != null && complaintLot != null)
                {
                    ServerUtil.sendComplaint(complaintBody,complaintLot);
                    Toast.makeText(getActivity(), "Complaint is sent to us. A user reported", Toast.LENGTH_SHORT).show();
                }
                else if(radiobutton2.isChecked() && complaintBody != null)
                {

                    ServerUtil.sendComplaint(complaintBody);
                    Toast.makeText(getActivity(), "Thanks for the feedback ", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(), "Nothing sent", Toast.LENGTH_SHORT).show();

			}
		});
	}
}
