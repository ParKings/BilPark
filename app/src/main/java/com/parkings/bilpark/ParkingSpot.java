package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

/**
 * Created by uğur on 15.04.2018.
 * Last edited by Emre Acarturk.
 *
 * @author Uğur
 * @version 25.04.2018.0
 */
public class ParkingSpot {
	//CORNERS ARE ASSIGNED IN COUNTERCLOCKWISE DIRECTION
	//SIDE 03 AND 12 ARE LONGER ONES
	//SIDE 01 AND 32 ARE SHORTER ONES
	// Constants
	@Exclude
	public static final String isParkedTag = "isParked";
	@Exclude
	public static final String centerTag = "center";

	//properties
	private boolean isParked;
	private LatLng center;
	@Exclude
	private LatLng[] corners;

	//constructors
	public ParkingSpot() {
		// Required for Firebase
	}

	public ParkingSpot(LatLng[] corners) {
		this.corners = corners;
		center = new LatLng(
				(corners[2].latitude
						+ corners[3].latitude
						+ ((corners[1].latitude - corners[2].latitude) / 3)
						+ ((corners[0].latitude - corners[3].latitude) / 3)) / 2,
				(corners[2].longitude
						+ corners[3].longitude
						+ ((corners[1].longitude - corners[2].longitude) / 3)
						+ ((corners[0].longitude - corners[3].longitude) / 3)) / 2);
		isParked = false;
	}

	//methods
	public void setParked(boolean isParked) {
		this.isParked = isParked;
	}

	public boolean getParked() {
		return isParked;
	}

	@Exclude
	public LatLng[] getCorners() {
		return corners;
	}

	public LatLng getCenter() {
		return center;
	}

	@Exclude
	public boolean contains(LatLng latLng) {
		double totalArea = 0.5 * Math.abs(corners[0].latitude * corners[1].longitude +
				corners[1].latitude * corners[2].longitude +
				corners[2].latitude * corners[3].longitude +
				corners[3].latitude * corners[0].longitude -
				corners[1].latitude * corners[0].longitude -
				corners[2].latitude * corners[1].longitude -
				corners[3].latitude * corners[2].longitude -
				corners[0].latitude * corners[3].longitude);
		double resultingArea = 0;
		for (int i = 0; i < 4; i++) {
			resultingArea += 0.5 * Math.abs(latLng.latitude * corners[0 + i].longitude +
					corners[0 + i].latitude * corners[(1 + i) % 4].longitude +
					corners[(1 + i) % 4].latitude * latLng.longitude -
					corners[0 + i].latitude * latLng.longitude -
					corners[(1 + i) % 4].latitude * corners[0 + i].longitude -
					latLng.latitude * corners[(1 + i) % 4].longitude);
		}
		return resultingArea <= totalArea;
	}
}
