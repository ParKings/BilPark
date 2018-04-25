package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

/**
 * Created by uğur on 15.04.2018.
 * Last edited by Emre on 25.04.2018.
 *
 * Last edited to add Firebase compatibility.
 *
 * @version 2018.04.25.0
 */
public class ParkingLot {
	// Note: CORNERS ARE ASSIGNED IN COUNTERCLOCKWISE DIRECTION
	// Properties
	@Exclude
	LatLng[] corners;
	int occupiedSlots;
	int totalSlots;
	@Exclude
	private String name;

	//constructors
	public ParkingLot() {
		// Required by Firebase
	}

	public ParkingLot(int totalSlots, LatLng[] corners, String name) {
		this.corners = corners;
		this.totalSlots = totalSlots;
		occupiedSlots = 0;
		this.name = name;
	}

	//methods
	@Exclude
	public boolean contains(LatLng latLng) {
		double totalArea = 0.5 * Math.abs(
				corners[0].latitude * corners[1].longitude +
				corners[1].latitude * corners[2].longitude +
				corners[2].latitude * corners[3].longitude +
				corners[3].latitude * corners[0].longitude -
				corners[1].latitude * corners[0].longitude -
				corners[2].latitude * corners[1].longitude -
				corners[3].latitude * corners[2].longitude -
				corners[0].latitude * corners[3].longitude);
		double resultingArea = 0;
		for (int i = 0; i < 4; i++) {
			resultingArea += 0.5 * Math.abs(
					latLng.latitude * corners[0 + i].longitude +
					corners[0 + i].latitude * corners[(1 + i) % 4].longitude +
					corners[(1 + i) % 4].latitude * latLng.longitude -
					corners[0 + i].latitude * latLng.longitude -
					corners[(1 + i) % 4].latitude * corners[0 + i].longitude -
					latLng.latitude * corners[(1 + i) % 4].longitude);
		}
		return resultingArea <= totalArea;
	}

	@Exclude
	public boolean isEmpty () {
		return occupiedSlots <= 0;
	}

	@Exclude
	public boolean isFull() {
		return occupiedSlots >= totalSlots;
	}

	@Exclude
	public int getRatio() {
		return (int) (100.0 * occupiedSlots / totalSlots);
	}
}
