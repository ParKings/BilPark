package com.parkings.bilpark;

import com.example.uur.bilpark.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by uğur on 15.04.2018.
 * Last edited by Emre Acarturk. Added JavaDoc.
 *
 * @author Uğur
 * @version 28.04.2018.0
 */
public class ParkingSpot {
	//CORNERS ARE ASSIGNED IN COUNTERCLOCKWISE DIRECTION
	//SIDE 03 AND 12 ARE LONGER ONES
	//SIDE 01 AND 32 ARE SHORTER ONES
	// Constants
	@Exclude
	public static final String isParkedTag   = "isParked";
	@Exclude
	public static final String centerTag     = "center";

	//properties
	private boolean isParked;
	private LatLng center;
	@Exclude
	private LatLng[] corners;
	public static HashMap<LatLng, GroundOverlayOptions> dots;

	//constructors
	/**
	 * Default constructor needed for Firebase integration
	 */
	public ParkingSpot() {}

	/**
	 * Secondary constructor initializing parameters.
	 *
	 * @param corners The corners of this parking spot
	 */
	public ParkingSpot(LatLng[] corners) {
		dots = new HashMap<>();
		this.corners = corners;
		center = new LatLng(
				(corners[2].latitude
						+ corners[3].latitude
						+ (Math.abs(corners[1].latitude - corners[2].latitude) / 3)
						+ (Math.abs(corners[0].latitude - corners[3].latitude) / 3)) / 2,
				(corners[2].longitude
						+ corners[3].longitude
						+ (Math.abs(corners[1].longitude - corners[2].longitude) / 3)
						+ (Math.abs(corners[0].longitude - corners[3].longitude) / 3)) / 2);
		isParked = false;
		LatLngBounds dotBounds = new LatLngBounds(
				new LatLng(center.latitude - 0.000012, center.longitude - 0.000012 ),       // South west corner
				new LatLng(center.latitude + 0.000012, center.longitude + 0.000012 ));      // North east corner
		dots.put(center, new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(R.raw.greendot))
				.positionFromBounds(dotBounds)
				.transparency(0f));
	}

	//methods

	/**
	 * Sets the spot's status to occupied by storing the parking date
	 */
	public void park() {
		isParked = true;
	}

	/**
	 * Sets the spot's status to unoccupied by storing the "minimum string" as date
	 */
	public void unpark () {
		isParked = false;
	}

	/**
	 * Returns if the parking lot parked.
	 *
	 * @return Is the parking lot parked?
	 */
	public boolean isParked() {
		return isParked;
	}

	/**
	 * Returns the spot's corners
	 *
	 * @return The corners of this spot
	 */
	@Exclude
	public LatLng[] getCorners() {
		return corners;
	}

	/**
	 * Returns the coordinates of this parking spot.
	 *
	 * @return Coordinates of this parking spot's center
	 */
	public LatLng getCenter() {
		return center;
	}

	public String toString() {
		return ( "\nCENTER LAT: " + center.latitude + "\nCENTER LONG: " + center.longitude + "\nPARKED: " + isParked );
	}

	/**
	 * Returns true if the given point is effectively within the spot's reach
	 *
	 * @param latLng The coordinate to be checked
	 * @return true; if the given point is effectively within the spot's reach
	 *         false; else
	 */
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
