package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

/**
 * Created by uğur on 15.04.2018.
 * Last edited by Emre on 25.04.2018.
 *
 * Last edited to add JavaDoc.
 *
 * @author Ugur
 * @version 28.04.2018.0
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
	/**
	 * Default constructor needed for Firebase integration
	 */
	public ParkingLot() {}

	/**
	 * Secondary constructor initializing parameters.
	 *
	 * @param totalSlots Total number of slots in the given lot
	 * @param corners    The corners of the given lot
	 * @param name       Name, or, tag, of the given lot
	 */
	public ParkingLot(int totalSlots, LatLng[] corners, String name) {
		this.corners = corners;
		this.totalSlots = totalSlots;
		occupiedSlots = 0;
		this.name = name;
	}

	//methods

	/**
	 * Yields the name of the lot
	 *
	 * @return name
	 */
	@Exclude
	public String getName() {
		return name;
	}

	/**
	 * Returns true if the given coordinates are within reasonable proximity of the given lot
	 * @param latLng The coordinate to be checked
	 * @return true;  if the given coordinates are within reasonable proximity of the given lot
	 *         false; otherwise
	 */
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

	/**
	 * Returns if the lot is empty
	 *
	 * @return Is the lot empty
	 */
	@Exclude
	public boolean isEmpty () {
		return occupiedSlots <= 0;
	}

	/**
	 * Returns if the lot is full
	 *
	 * @return Is the lot full
	 */
	@Exclude
	public boolean isFull() {
		return occupiedSlots >= totalSlots;
	}

	/**
	 * Returns the occupancy ratio for this lot
	 *
	 * @return The occupancy ratio for this lot
	 */
	@Exclude
	public int getRatio() {
		return (int) (100.0 * occupiedSlots / totalSlots);
	}
}
