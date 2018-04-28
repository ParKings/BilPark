package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by uğur on 15.04.2018.
 * Last edited by Emre on 28.04.2018.
 *
 * @author uğur
 * @version 2018.04.2018 : JavaDoc added.
 */
//CORNERS ARE ASSIGNED IN COUNTERCLOCKWISE DIRECTION
//SIDE 03 AND 12 ARE SHORTER ONES
//SIDE 01 AND 32 ARE LONGER ONES
public class ParkingRow {
	//properties
	private int spotNumber;
	LatLng[] corners;
	ParkingSpot[] parkingSpots;

	//constructor
	/**
	 * The constructor which constructs parking spots within a given parking row
	 *
	 * @param spotNumber How many parking slots exist in the given row
	 * @param corners    What are the 4 corners of the given row
	 */
	public ParkingRow(int spotNumber, LatLng[] corners) {
		this.spotNumber = spotNumber;
		this.corners = corners;
		parkingSpots = new ParkingSpot[spotNumber];
		for (int i = 0; i < spotNumber; i++) {
			double lat01difference = (corners[0].latitude - corners[1].latitude) / spotNumber;
			double lat32difference = (corners[3].latitude - corners[2].latitude) / spotNumber;
			double long01difference = (corners[0].longitude - corners[1].longitude) / spotNumber;
			double long32difference = (corners[3].longitude - corners[2].longitude) / spotNumber;
			LatLng[] parkCorners = {new LatLng(corners[1].latitude + lat01difference * (i + 1), corners[1].longitude + long01difference * (i + 1)),
					new LatLng(corners[1].latitude + lat01difference * i, corners[1].longitude + long01difference * i),
					new LatLng(corners[1].latitude + lat32difference * i, corners[1].longitude + long32difference * i),
					new LatLng(corners[1].latitude + lat32difference * (i + 1), corners[1].longitude + long32difference * (i + 1))};
			parkingSpots[i] = new ParkingSpot(parkCorners);
		}
	}
}
