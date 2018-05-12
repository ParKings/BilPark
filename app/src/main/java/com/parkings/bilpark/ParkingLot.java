package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

/**
 * Created by uğur on 15.04.2018.
 * Last edited by Emre on 25.04.2018.
 * <p>
 * Last edited to fix contains(LatLng).
 *
 * @author Ugur
 * @version 12.05.2018.0
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
	public ParkingLot() {
	}

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
	 *
	 * @param latLng The coordinate to be checked
	 * @return true;  if the given coordinates are within reasonable proximity of the given lot
	 * false; otherwise
	 */
	@Exclude
	public boolean contains(LatLng latLng) {
		return PolygonUtil.isInside(corners, 4, latLng);
	}

	/**
	 * Returns if the lot is empty
	 *
	 * @return Is the lot empty
	 */
	@Exclude
	public boolean isEmpty() {
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
