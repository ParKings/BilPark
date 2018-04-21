package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by uğur on 15.04.2018.
 */

public class ParkingLots {

	//properties
	private ParkingLot[] parkingLots;
	private ParkingRow[] parkingRows;
	private int totalSpots = 0;
	int occupiedSpots;

	/**
	 * Default constructor.
	 *
	 * @param parkingLots : All "ParkingLot"s to be added to this class
	 * @param parkingRows : All "ParkingRow"s to be added to this class
	 */
	public ParkingLots(ParkingLot[] parkingLots, ParkingRow[] parkingRows) {
		this.parkingLots = parkingLots;
		this.parkingRows = parkingRows;
		for (ParkingLot parkingLot : parkingLots) {
			totalSpots += parkingLot.totalSlots;
		}
		occupiedSpots = 0;
	}

	/**
	 * Called by the ServerUtil#park(..) method. Includes the necessary algorithm to park
	 * with a given location data. Updates the ParkingLot and ParkingSpace that has been
	 * used and returns the LatLng of the ParkingSpace.
	 * <p>
	 * <b>Note:</b> Returns null if the parking spot is already occupied or there is no such spot
	 * </p>
	 *
	 * @param location
	 * @return
	 */
	public LatLng park(LatLng location) {
		// Guaranteeing incrementation in parking lot scale
		for (ParkingLot parkingLot : parkingLots) {
			if (parkingLot.contains(location)) {
				parkingLot.occupiedSlots++;
			}
		}
		// Parking to a ParkingSlot
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				if (parkingSpot.contains(location) && !parkingSpot.getParked()) {
					parkingSpot.setParked(true);
					return new LatLng(
							(parkingSpot.corners[2].latitude
									+ parkingSpot.corners[3].latitude
									+ ((parkingSpot.corners[1].latitude - parkingSpot.corners[2].latitude) / 3)
									+ ((parkingSpot.corners[0].latitude - parkingSpot.corners[3].latitude) / 3)) / 2,
							(parkingSpot.corners[2].longitude
									+ parkingSpot.corners[3].longitude
									+ ((parkingSpot.corners[1].longitude - parkingSpot.corners[2].longitude) / 3)
									+ ((parkingSpot.corners[0].longitude - parkingSpot.corners[3].longitude) / 3)) / 2);
				}
			}
		}
		// If no appropriate ParkingSpot is found,
		return null;
	}
}
