package com.parkings.bilpark;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;


/**
 * A fragment which displays statistics of parking lots
 * in terms of day, week and month
 *
 * @author furkan & altan
 * @version 2018.05.11.0
 */
public class DetailedStatisticsFragment extends Fragment {

	//properties
	private LayoutInflater inflater;
	private LotStatistics lotStatistics;
	private String[] scheduleTypes;
	private ArrayAdapter<String> adapter;
	private Spinner spinner;
	private String currentLot;
	private TextView lotInfo;
	//private int percentage;
	private Button backButton;
	private Fragment fragment;
	private LineChart lineChart;
	private ArrayList<ILineDataSet> lineDataSets;
	private LineDataSet lineDataSet1_1;
	private LineDataSet lineDataSet1_2;
	private LineDataSet lineDataSet1_3;
	private LineDataSet lineDataSet2_1;
	private LineDataSet lineDataSet2_2;
	private LineDataSet lineDataSet2_3;
	private LineDataSet lineDataSet3_1;
	private LineDataSet lineDataSet3_2;
	private LineDataSet lineDataSet3_3;

	//methods
	/**
	 * Initializes the view related to this fragment
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		this.inflater = inflater;
		return inflater.inflate(R.layout.detailed_statistics_fragment, null);
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

		lineChart = view.findViewById(R.id.line_chart);
		lineChart.setVisibleXRangeMaximum(10f);
		lineChart.setBackgroundColor(Color.WHITE);

		ArrayList<Entry> yAxesNanoDaily = new ArrayList<>();
		ArrayList<Entry> yAxesNanoWeekly = new ArrayList<>();
		ArrayList<Entry> yAxesNanoMonthly = new ArrayList<>();
		ArrayList<Entry> yAxesUnamDaily = new ArrayList<>();
		ArrayList<Entry> yAxesUnamWeekly = new ArrayList<>();
		ArrayList<Entry> yAxesUnamMonthly = new ArrayList<>();
		ArrayList<Entry> yAxesMescidDaily = new ArrayList<>();
		ArrayList<Entry> yAxesMescidWeekly = new ArrayList<>();
		ArrayList<Entry> yAxesMescidMonthly = new ArrayList<>();

		int numDataPoints = 7;
		for(int i = 1; i <= numDataPoints; i++)
		{
			float nanoDaily = Float.parseFloat(String.valueOf(Math.pow(i - 1, 5) % 100));
			float unamDaily = Float.parseFloat(String.valueOf(Math.pow(i - 1, 4) % 100));
			float mescidDaily = Float.parseFloat(String.valueOf(Math.pow(i - 1, 6) % 100));
			yAxesNanoDaily.add(new Entry(i, nanoDaily));
			yAxesUnamDaily.add(new Entry(i, unamDaily));
			yAxesMescidDaily.add(new Entry(i, mescidDaily));
		}

		numDataPoints = 4;
		for(int i = 1; i <= numDataPoints; i++)
		{
			float nanoWeekly = Float.parseFloat(String.valueOf(Math.pow(i - 1, 3) % 100));
			float unamWeekly = Float.parseFloat(String.valueOf(Math.pow(i - 1, 2) % 100));
			float mescidWeekly = Float.parseFloat(String.valueOf(Math.pow(i - 1, 4) % 100));
			yAxesNanoWeekly.add(new Entry(i, nanoWeekly));
			yAxesUnamWeekly.add(new Entry(i, unamWeekly));
			yAxesMescidWeekly.add(new Entry(i, mescidWeekly));
		}

		numDataPoints = 12;
		for(int i = 1; i <= numDataPoints; i++)
		{
			float nanoMonthy = Float.parseFloat(String.valueOf(Math.sqrt(i - 1) % 100));
			float unamMonthy = Float.parseFloat(String.valueOf(Math.pow(i - 1, 0.33) % 100));
			float mescidMonthy = Float.parseFloat(String.valueOf(Math.pow(i - 1, 2) % 100));;
			yAxesNanoMonthly.add(new Entry(i, nanoMonthy));
			yAxesUnamMonthly.add(new Entry(i, unamMonthy));
			yAxesMescidMonthly.add(new Entry(i, mescidMonthy));
		}

		lineDataSet1_1 = new LineDataSet(yAxesNanoDaily, "Nanotam Park");
		lineDataSet1_1.setColor(Color.BLUE);

		lineDataSet1_2 = new LineDataSet(yAxesNanoWeekly, "Nanotam Park");
		lineDataSet1_2.setColor(Color.BLUE);

		lineDataSet1_3 = new LineDataSet(yAxesNanoMonthly, "Nanotam Park");
		lineDataSet1_3.setColor(Color.BLUE);

		lineDataSet2_1 = new LineDataSet(yAxesUnamDaily, "Unam Park");
		lineDataSet2_1.setColor(Color.RED);

		lineDataSet2_2 = new LineDataSet(yAxesUnamWeekly, "Unam Park");
		lineDataSet2_2.setColor(Color.RED);

		lineDataSet2_3 = new LineDataSet(yAxesUnamMonthly, "Unam Park");
		lineDataSet2_3.setColor(Color.RED);

		lineDataSet3_1 = new LineDataSet(yAxesMescidDaily, "Mescid Park");
		lineDataSet3_1.setColor(Color.GREEN);

		lineDataSet3_2 = new LineDataSet(yAxesMescidWeekly, "Mescid Park");
		lineDataSet3_2.setColor(Color.GREEN);

		lineDataSet3_3 = new LineDataSet(yAxesMescidMonthly, "Mescid Park");
		lineDataSet3_3.setColor(Color.GREEN);

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
		adapter = new EnhancedArrayAdapter(getActivity(), R.layout.custom_line, scheduleTypes, inflater);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
												int index, long id) {

				lotInfo.setText(scheduleTypes[index] + " Statistics For " + currentLot);
				getRoundedRatio(index);

				//percentage = getRoundedRatio(index);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}

	/**
	 * Rounds the numerical values obtained from getRatio() method
	 *
	 * @param index
	 */
	public void getRoundedRatio(int index) {
		/*int roundedRatio = (int) (getRatio(index) + 0.5);
		if (roundedRatio > 100)
			roundedRatio = 100;

		return roundedRatio;*/
		lineDataSets = new ArrayList<>();
		if (index == 0 && currentLot.equals("Nanotam")) {
			lineDataSets.add(lineDataSet1_1);
		} else if (index == 1 && currentLot.equals("Nanotam")) {
			lineDataSets.add(lineDataSet1_2);
		} else if (index == 2 && currentLot.equals("Nanotam")) {
			lineDataSets.add(lineDataSet1_3);
		} else if (index == 0 && currentLot.equals("Unam")) {
			lineDataSets.add(lineDataSet2_1);
		} else if (index == 1 && currentLot.equals("Unam")) {
			lineDataSets.add(lineDataSet2_2);
		} else if (index == 2 && currentLot.equals("Unam")) {
			lineDataSets.add(lineDataSet2_3);
		} else if (index == 0 && currentLot.equals("Mescid")) {
			lineDataSets.add(lineDataSet3_1);
		} else if (index == 1 && currentLot.equals("Mescid")) {
			lineDataSets.add(lineDataSet3_2);
		} else if (index == 2 && currentLot.equals("Mescid")) {
			lineDataSets.add(lineDataSet3_3);
		}
		lineChart.setData(new LineData(lineDataSets));
		lineChart.invalidate();
	}

	/**
	 * Get particular ratios from LotStatistics class
	 *
	 * @param index
	 * @return ratio
	 */
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
	 * An initializer for this fragment
	 *
	 * @param lotName
	 */
	public void initialize(String lotName) {
		lotStatistics = Statistics.getLotStatistics(lotName);
		scheduleTypes = lotStatistics.getScheduleTypes();
		currentLot = lotName;
	}
}