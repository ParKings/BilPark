package com.parkings.bilpark;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * Opens up and oversees the floating action buttons and their functionality.
 * Extends Fragment, hence is bound to its related activity, i.e. MainActivity.
 * <p>
 * Last edited by Emre Acarturk. Added JavaDocs.
 *
 * @author Furkan
 * @version 2018.05.11.0
 */
public class ActionButtonFragment extends Fragment {

	//properties
	private MainActivity activity;
	private ServerUtil serverUtil;
	LatLng keyf;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.action_button, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		activity = (MainActivity) getActivity();
		serverUtil = ServerUtil.getInstance();
		FloatingActionButton fab = view.findViewById(R.id.fab);
		fab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				activity.getUserLocation();
				activity.polygonClicked = false;
				if ( activity.firstPush ) {
					activity.secondPush = true;
				}
				if ( activity.secondPush == false ) {
					activity.firstPush = true;
				}
				if ( !activity.secondPush && activity.firstPush ) {
					activity.addPolygon("nanotam");
				}
				//activity.addPolygon( "mescid");
				//activity.addPolygon( "unam");
			}
		});

		FloatingActionButton fab2 = view.findViewById(R.id.fab2);
		fab2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				/*
				keyf = serverUtil.park(activity.test);
				if ( keyf == null ) {
					Toast.makeText( activity, "batırdın", Toast.LENGTH_SHORT).show();
				} */
				if (ContextCompat.checkSelfPermission( activity, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

					ActivityCompat.requestPermissions( activity, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
							1 );
				}
				//UNCOMMENT LATER activity.parkLocation = activity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (activity.parkLocation != null && !activity.userParked) {
					//activity.userParked = true;
					serverUtil.park( new LatLng( activity.parkLocation.getLatitude(), activity.parkLocation.getLongitude() ) );
				}
				//CHECK HERE
				else if (activity.parkLocation == null && !activity.userParked) {
					while ( activity.parkLocation == null ) {
						activity.parkLocation = activity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					}
					//activity.userParked = true;
					activity.test = serverUtil.park( new LatLng( activity.parkLocation.getLatitude(), activity.parkLocation.getLongitude() ) );
				}
				else {
					Toast.makeText( activity, "You are already parked", Toast.LENGTH_SHORT).show();
				}
				if ( activity.userParked ) {
					serverUtil.unpark( new LatLng( activity.parkLocation.getLatitude(), activity.parkLocation.getLongitude() ) );
				}
			}
		});
	}
}