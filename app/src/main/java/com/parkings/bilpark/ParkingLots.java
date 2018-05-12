package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;

/**
 * <p>
 * Created by uğur on 15.04.2018.
 * Last modified by Emre on 28.04.2018, added warning statement.
 * </p>
 * <p>
 * <b>Warning:</b> Lost functionality, a.k.a. not backwards compatible
 * </p>
 *
 * @deprecated Functionality transferred to ServerUtil methods of same name.
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
		// Incrementing in parking lot scale
		for (ParkingLot parkingLot : parkingLots)
			if (parkingLot.contains(location))
				parkingLot.occupiedSlots++;

		// Parking to a ParkingSlot
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				if (parkingSpot.contains(location) /*&& !parkingSpot.getParkingSpots(null)*/) {
					/*parkingSpot.setParked(true);*/
					return parkingSpot.getCenter();
				}
			}
		}
		// If no appropriate ParkingSpot is found,
		return null;
	}

	public LatLng unpark(LatLng location) {
		// Decreasing in parking lot scale
		for (ParkingLot parkingLot : parkingLots)
			if (parkingLot.contains(location))
				parkingLot.occupiedSlots--;

		// Unparking to a ParkingSlot
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				if (parkingSpot.contains(location) /*&& parkingSpot.getParkingSpots()*/) {
					/*parkingSpot.setParked(false);*/
					return parkingSpot.getCenter();
				}
			}
		}
		// If no appropriate ParkingSpot is found,
		return null;
	}
}
