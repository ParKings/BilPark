package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by uğur on 15.04.2018.
 */
public class ParkingSpot {
	//CORNERS ARE ASSIGNED IN COUNTERCLOCKWISE DIRECTION
	//SIDE 03 AND 12 ARE LONGER ONES
	//SIDE 01 AND 32 ARE SHORTER ONES
	//properties
	boolean isParked;
	LatLng[] corners;

	//constructors
	public ParkingSpot(LatLng[] corners) {
		this.corners = corners;
		isParked = false;
	}

	//methods
	public void setParked(boolean isParked) {
		this.isParked = isParked;
	}

	public boolean getParked() {
		return isParked;
	}

	public LatLng[] getCorners() {
		return corners;
	}

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
