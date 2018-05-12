package com.parkings.bilpark;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 * <p>
 * ToDo: Fill in the @author !!!
 *
 * @author
 * @version 2018.05.11.0
 */
public class DetailedStatisticsFragment extends Fragment {

	private class EnhancedArrayAdapter extends ArrayAdapter<String> {

		public EnhancedArrayAdapter(Context context, int resourceId, String[] list) {
			super(context, resourceId, list);
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
			View view = getLayoutInflater().inflate(R.layout.custom_line, null);
			TextView text = view.findViewById(R.id.anOption);
			text.setText(getItem(index));

			return view;
		}
	}

	//properties
	private LotStatistics lotStatistics;
	private String[] scheduleTypes;
	private ArrayAdapter<String> adapter;
	private Spinner spinner;
	private String currentLot;
	private TextView lotInfo;
	private int percentage;
	private Button backButton;
	private Fragment fragment;
	private LineChart lineChart;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.detailed_statistics_fragment, null);
	}

	// TODO: Rename method, update argument and hook method into UI event
	/*public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	  /**
     * Initializes and displays the list of
     * the parking lots after the view is created.
     * Gets the information about the parking lot
     * to show its statistics when the parking lot
     * is selected.
     *
     * @param view
     * @param savedInstanceState
     */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		lotInfo = view.findViewById(R.id.lotname_info);
		backButton = view.findViewById(R.id.back_button);

		lineChart = (LineChart) view.findViewById(R.id.line_chart);

		ArrayList<String> xAxes = new ArrayList<>();
		ArrayList<Entry> yAxesNano = new ArrayList<>();
		ArrayList<Entry> yAxesUnam = new ArrayList<>();
		ArrayList<Entry> yAxesMescid = new ArrayList<>();

		int x = 0;
		int numDataPoints = 10;
		for(int i = 1; i <= numDataPoints; i++)
		{
			float nanoFunction = Float.parseFloat(String.valueOf(Math.sqrt(x)));
			float unamFunction = Float.parseFloat(String.valueOf(Math.pow(x, 3)));
			float mescidFunction = Float.parseFloat(String.valueOf(Math.pow(x,5)));
			x += 1;
			yAxesNano.add(new Entry(nanoFunction,i));
			yAxesUnam.add(new Entry(unamFunction,i));
			yAxesMescid.add(new Entry(mescidFunction,i));
			xAxes.add(String.valueOf(x));
		}
		String[] xaxes = new String[xAxes.size()];
		for (int i = 0; i < xAxes.size(); i++)
		{
			xaxes[i] = xAxes.get(i).toString();
		}
		ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

		LineDataSet lineDataSet1 = new LineDataSet(yAxesNano, "Nano Park");
		lineDataSet1.setDrawCircles(false);
		lineDataSet1.setColor(Color.BLUE);

		LineDataSet lineDataSet2 = new LineDataSet(yAxesUnam, "Unam Park");
		lineDataSet1.setDrawCircles(false);
		lineDataSet2.setColor(Color.RED);

		LineDataSet lineDataSet3 = new LineDataSet(yAxesMescid, "Mescid Park");
		lineDataSet1.setDrawCircles(false);
		lineDataSet3.setColor(Color.YELLOW);
		lineDataSets.add(lineDataSet1);
		lineDataSets.add(lineDataSet2);
		lineDataSets.add(lineDataSet3);

		lineChart.setData(new LineData(lineDataSets));
		lineChart.setVisibleXRangeMaximum(10f);

		lineChart.setBackgroundColor(Color.WHITE);


		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment = new StatisticsFragment();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.statistics, fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});

		spinner = view.findViewById(R.id.spinner);
		adapter = new EnhancedArrayAdapter(getActivity(), R.layout.custom_line, scheduleTypes);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
												int index, long id) {

				lotInfo.setText(scheduleTypes[index] + " Statistics For " + currentLot);

				//percentage = getRoundedRatio(index);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}

	public int getRoundedRatio(int index) {
		int roundedRatio = (int) (getRatio(index) + 0.5);
		if (roundedRatio > 100)
			roundedRatio = 100;

		return roundedRatio;
	}

	public double getRatio(int index) {
		double ratio = 0;
		if (index == 0)
			ratio = 100 * lotStatistics.getDaily().get(currentLot);
		else if (index == 1)
			ratio = 100 * lotStatistics.getWeekly().get(currentLot);
		else if (index == 2)
			ratio = 100 * lotStatistics.getMonthly().get(currentLot);

		return ratio;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	/*public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}*/
	public void initialize(String lotName) {
		lotStatistics = Statistics.getLotStatistics(lotName);
		scheduleTypes = lotStatistics.getScheduleTypes();
		currentLot = lotName;
	}
}