package com.parkings.bilpark;

import android.graphics.Color;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by uğur on 15.04.2018.
 * Last edited by Emre Acarturk.
 *
 * @author Uğur
 * @version 12.05.2018.0
 */
public class ParkingSpot {


	//CORNERS ARE ASSIGNED IN COUNTERCLOCKWISE DIRECTION
	//SIDE 12 AND 03 ARE LONGER ONES
	//SIDE 01 AND 32 ARE SHORTER ONES
	// Constants
	@Exclude
	public static final String isParkedTag = "isParked";
	@Exclude
	public static final String centerTag = "center";

	//properties
	public boolean isParked;
	private double latitude;
	private double longitude;

	@Exclude
	private LatLng center;
	@Exclude
	private LatLng[] corners;
	@Exclude
	public static HashMap<LatLng, GroundOverlayOptions> dots = new HashMap<>();
  //COUNTER IS FOR CONTROLLING THE NUMBER OF GREEN DOTS
    @Exclude
    static int counter = 0;
	@Exclude
	static LatLng[][] polytest = new LatLng[17][4];

	//constructors

	/**
	 * Default constructor needed for Firebase integration
	 */
	public ParkingSpot() {

	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Secondary constructor initializing parameters.
	 *
	 * @param corners The corners of this parking spot
	 */
	public ParkingSpot(LatLng[] corners) {
		if ( counter < 17 ) {
			polytest[counter] = corners;
		}
		counter++;
		this.corners = corners;
		latitude = (corners[0].latitude + corners[2].latitude) / 2;
		longitude = (corners[0].longitude + corners[2].longitude) / 2;
		isParked = false;
		center = getCenter();
		LatLngBounds dotBounds = new LatLngBounds(

				new LatLng(center.latitude - 0.000012, center.longitude - 0.000012 ),       // South west corner
				new LatLng(center.latitude + 0.000012, center.longitude + 0.000012 ));      // North east corner
    //COUNTER IS FOR CONTROLLING THE NUMBER OF GREEN DOTS

		if ( ( counter <= 18 && counter > 16 ) || ( counter >= 0 && counter < 2 ) ) {
			dots.put(center, new GroundOverlayOptions()
					.image(BitmapDescriptorFactory.fromResource(R.raw.greendot))
					.positionFromBounds(dotBounds)
					.transparency(0f));
		}
	}

	//methods

	/**
	 * Sets the spot's status to occupied by storing the parking date
	 */
	@Exclude
	public void park() {
		isParked = true;
	}

	/**
	 * Sets the spot's status to unoccupied by storing the "minimum string" as date
	 */
	@Exclude
	public void unpark() {
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
	@Exclude
	public LatLng getCenter() {
		return center = new LatLng(latitude, longitude);
	}

	@Exclude
	public String toString() {
		return ("\nCENTER LAT: " + center.latitude + "\nCENTER LONG: " + center.longitude + "\nPARKED: " + isParked);
	}

	/**
	 * Returns true if the given point is effectively within the spot's reach
	 *
	 * @param latLng The coordinate to be checked
	 * @return true; if the given point is effectively within the spot's reach
	 * false; else
	 */
	@Exclude
	public boolean contains(LatLng latLng) {
		return PolygonUtil.isInside(corners, 4, latLng);
	}
}
