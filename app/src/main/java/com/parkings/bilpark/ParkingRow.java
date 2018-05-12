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
	double lat01difference;
	double lat32difference;
	double long01difference;
	double long32difference;

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
		lat01difference = corners[1].latitude - corners[0].latitude;
		lat32difference = corners[2].latitude - corners[3].latitude;
		long01difference = corners[1].longitude - corners[0].longitude;
		long32difference = corners[2].longitude - corners[3].longitude;
		for (int i = 0; i < spotNumber; i++) {
			parkingSpots[i] = new ParkingSpot(new LatLng[] {
					new LatLng( corners[0].latitude + lat01difference * (i) / spotNumber,
							corners[0].longitude + long01difference * (i) / spotNumber),
					new LatLng( corners[0].latitude + lat01difference * (i + 1) / spotNumber,
							corners[0].longitude + long01difference * (i + 1) / spotNumber),
					new LatLng( corners[3].latitude + lat32difference * (i + 1) / spotNumber,
							corners[3].longitude + long32difference * (i + 1) / spotNumber),
					new LatLng( corners[3].latitude + lat32difference * (i) / spotNumber,
							corners[3].longitude + long32difference * (i) / spotNumber)});
		}
	}
}
