package com.parkings.bilpark;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by uğur on 15.04.2018.
 * Last edited by Emre Acarturk. Fixed center finding and containment algorithms.
 *
 * Reused C++ code from:
 * https://www.geeksforgeeks.org/how-to-check-if-a-given-point-lies-inside-a-polygon/
 *
 * Used an algorithm from:
 * http://www.dcs.gla.ac.uk/~pat/52233/slides/Geometry1x1.pdf.
 *
 * @author Uğur
 * @version 12.05.2018.0
 */
public class ParkingSpot {
	//CORNERS ARE ASSIGNED IN COUNTERCLOCKWISE DIRECTION
	//SIDE 03 AND 12 ARE LONGER ONES
	//SIDE 01 AND 32 ARE SHORTER ONES
	// Constants
	@Exclude
	public static final String isParkedTag = "isParked";
	@Exclude
	public static final String centerTag = "center";

	//properties
	private boolean isParked;
	private LatLng center;
	@Exclude
	private LatLng[] corners;
	public static HashMap<LatLng, GroundOverlayOptions> dots = new HashMap<>();
  //COUNTER IS FOR CONTROLLING THE NUMBER OF GREEN DOTS
	static int counter = 0;

	//constructors

	/**
	 * Default constructor needed for Firebase integration
	 */
	public ParkingSpot() {
	}

	/**
	 * Secondary constructor initializing parameters.
	 *
	 * @param corners The corners of this parking spot
	 */
	public ParkingSpot(LatLng[] corners) {
		counter++;
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
    //COUNTER IS FOR CONTROLLING THE NUMBER OF GREEN DOTS
		if ( counter < 36 && counter > 31 ) {
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
	public void park() {
		isParked = true;
	}

	/**
	 * Sets the spot's status to unoccupied by storing the "minimum string" as date
	 */
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
	public LatLng getCenter() {
		return center;
	}

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
		return isInside(corners, 4, latLng);
	}

	/*
		The following static part of this class is converted from the C++ code from
		https://www.geeksforgeeks.org/how-to-check-if-a-given-point-lies-inside-a-polygon/
	 */
	/*
	 * Given three colinear LatLngs p, q, r, the function checks if
	 * LatLng q lies on line segment 'pr'
	 */
	private static boolean onSegment(LatLng p, LatLng q, LatLng r)
	{
		if (q.latitude <= Math.max(p.latitude, r.latitude) && q.latitude >= Math.min(p.latitude, r.latitude) &&
				q.longitude <= Math.max(p.longitude, r.longitude) && q.longitude >= Math.min(p.longitude, r.longitude))
			return true;
		return false;
	}

	/*
		To find orientation of ordered triplet (p, q, r).
		The function returns following values
		0 --> p, q and r are colinear
		1 --> Clockwise
		2 --> Counterclockwise
	*/
	private static int orientation(LatLng p, LatLng q, LatLng r)
	{
		double val = (q.longitude - p.longitude) * (r.latitude - q.latitude) -
				(q.latitude - p.latitude) * (r.longitude - q.longitude);

		if (Math.abs(val) < EPSILON) return 0;  // colinear
		return (val > 0)? 1: 2; // clock or counterclock wise
	}

	/*
		The function that returns true if line segment 'p1q1'
		and 'p2q2' intersect.
	*/
	private static boolean doIntersect(LatLng p1, LatLng q1, LatLng p2, LatLng q2)
	{
		// Find the four orientations needed for general and
		// special cases
		int o1 = orientation(p1, q1, p2);
		int o2 = orientation(p1, q1, q2);
		int o3 = orientation(p2, q2, p1);
		int o4 = orientation(p2, q2, q1);

		// General case
		if (o1 != o2 && o3 != o4)
			return true;

		// Special Cases
		// p1, q1 and p2 are colinear and p2 lies on segment p1q1
		if (o1 == 0 && onSegment(p1, p2, q1)) return true;

		// p1, q1 and p2 are colinear and q2 lies on segment p1q1
		if (o2 == 0 && onSegment(p1, q2, q1)) return true;

		// p2, q2 and p1 are colinear and p1 lies on segment p2q2
		if (o3 == 0 && onSegment(p2, p1, q2)) return true;

		// p2, q2 and q1 are colinear and q1 lies on segment p2q2
		if (o4 == 0 && onSegment(p2, q1, q2)) return true;

		return false; // Doesn't fall in any of the above cases
	}

	// Returns true if the LatLng p lies inside the polygon[] with n vertices
	private static boolean isInside(LatLng polygon[], int n, LatLng p)
	{
		// There must be at least 3 vertices in polygon[]
		if (n < 3)  return false;

		// Create a LatLng for line segment from p to infinite
		LatLng extreme = new LatLng(Double.MAX_VALUE, p.longitude);

		// Count intersections of the above line with sides of polygon
		int count = 0, i = 0;
		do
		{
			int next = (i+1)%n;

			// Check if the line segment from 'p' to 'extreme' intersects
			// with the line segment from 'polygon[i]' to 'polygon[next]'
			if (doIntersect(polygon[i], polygon[next], p, extreme))
			{
				// If the LatLng 'p' is colinear with line segment 'i-next',
				// then check if it lies on segment. If it lies, return true,
				// otherwise false
				if (orientation(polygon[i], p, polygon[next]) == 0)
					return onSegment(polygon[i], p, polygon[next]);

				count++;
			}
			i = next;
		} while (i != 0);

		// Return true if count is odd, false otherwise
		return count%2 == 1;
	}
}
