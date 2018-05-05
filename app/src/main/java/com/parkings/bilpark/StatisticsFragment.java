package com.parkings.bilpark;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
public class StatisticsFragment extends Fragment {

    private ParkingLot[] lots;
    private String[] listOfLots;
    private ListView listView;
    private LotStatistics lotStatistics;
    private ArrayAdapter<String> adapter;

    //methods

    /**
     * Recevies and stores parking lots and
     * their names while the fragment is being created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lots = ServerUtil.getParkingLots();
        listOfLots = new String[lots.length];
        for(int i = 0; i < lots.length; i++)
            listOfLots[i] = lots[i].getName();
    }

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
        return inflater.inflate(R.layout.statistics_fragment, null);
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

        listView = view.findViewById(R.id.listView1);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, listOfLots);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                lotStatistics = Statistics.getLot(lots[index]);
            }
        });
    }
}