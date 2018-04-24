package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.HashMap;

/**
 * <p>
 * Includes methods providing the app with connectivity to the Firebase services.
 * Created on 2018.04.20 by Emre Acarturk.
 * ToDo: Utilize everything down to (including) the getStatistics(String, String) method.
 * </p>
 * <p>
 * Includes code written by Uğur Yılmaz on 15.04.2018 (Park method and the constructor).
 * </p>
 * @author Emre Acarturk
 * @version 2018.04.24.0
 */
public class ServerUtil {

	// Constants
	/**
	 * A reference to the statistics section of the database
	 */
	private static final DatabaseReference statisticsReference;
	/**
	 * A reference to the parking data section of the database
	 */
	private static final DatabaseReference parkingDataReference;

	/**
	 * String tag for reaching root's statistics child
	 */
	private static final String statisticsTag = "statistics";
	/**
	 * String tag for reaching root's parking data child
	 */
	private static final String parkingDataTag = "parkingdata";

	/**
	 * Tag for reaching the "nanotam" lot from parking data and
	 */
	public static final String nanotamLotTag = "nanotam";
	/**
	 * Tag for reaching the "unam" lot from parking data and
	 */
	public static final String unamLotTag = "unam";
	/**
	 * Tag for reaching the "mescid" lot from parking data and
	 */
	public static final String mescidLotTag = "mescid";

	//properties
	private static boolean initialized;
	private static ParkingLot[] parkingLots;
	private static ParkingRow[] parkingRows;
	private static HashMap<String, Double> occupancyData;
	private static int totalSpots = 0;
	private static int occupiedSpots;

	/*  Eject something alike.

		// Read from the database
		myRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// This method is called once with the initial value and again
				// whenever data at this location is updated.
				String value = dataSnapshot.getValue(String.class);
				Log.d(TAG, "Value is: " + value);
			}

			@Override
			public void onCancelled(DatabaseError error) {
				// Failed to read value
				Log.w(TAG, "Failed to read value.", error.toException());
			}
		});

	 */

	// Static initializer
	static {
		// The method "initLotsAndRows" is not yet called
		initialized = false;
		occupancyData = new HashMap<>();

		// Database and reference object initializations
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference rootReference = firebaseDatabase.getReference();

		// ToDo: Build the necessary lots & rows. Then add them.
		initLotsAndRows(null, null);

		// Main children, from root
		statisticsReference = rootReference.child(statisticsTag);
		parkingDataReference = rootReference.child(parkingDataTag);

		// Statistics' children; lots
		statisticsReference.child(nanotamLotTag);
		statisticsReference.child(unamLotTag);
		statisticsReference.child(mescidLotTag);

		;
	}

	// Static methods

	/**
	 * Parks the car and sends the parking data to the database. Includes the necessary algorithm
	 * to park with a given latLng data. Updates the ParkingLot and ParkingSpace that has been
	 * used and returns the LatLng of the ParkingSpace.
	 * <p>
	 * <p>
	 * <b>Note 1:</b> Returns null if the parking spot is already occupied or there is no such spot
	 * </p>
	 * <p>
	 * <b>Note 2:</b> If the requested ParkingSlot is already occupied tries alternative paths and if
	 * all fail, registers the car to the ParkingLot itself.
	 * </p>
	 *
	 * @param latLng The coordinates to park the car. This value does not need to be equal to any
	 *               specific parking slot's coordinate, but rather, the according lot is found by
	 *               the algorithm.
	 * @return null, if no appropriate ParkingSlot is found;
	 * LatLng object of the parked ParkingSlot, otherwise
	 */
	protected static LatLng park(LatLng latLng) {
		// Incrementing in parking lot scale
		for (ParkingLot parkingLot : parkingLots)
			if (parkingLot.contains(latLng))
				parkingLot.occupiedSlots++;

		// Parking to a ParkingSlot
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				if (parkingSpot.contains(latLng) && !parkingSpot.getParked()) {
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

	/**
	 * Unparks the car and sends the relevant data to the database. Includes the necessary algorithm
	 * to unpark with a given latLng data. Updates the ParkingLot and ParkingSpace that has been
	 * used and returns the LatLng of the ParkingSpace.
	 * <p>
	 * <p>
	 * <b>Note 1:</b> Returns null if there is no such spot
	 * </p>
	 * <p>
	 * <b>Note 2:</b> If the requested ParkingSlot is already empty, tries alternative paths and if
	 * all fail, de-registers the car to the ParkingLot itself.
	 * </p>
	 *
	 * @param latLng The coordinates to unpark the car. This value does not need to be equal to any
	 *               specific parking slot's coordinate, but rather, the according lot is found by
	 *               the algorithm.
	 * @return null, if no appropriate ParkingSlot is found;
	 * LatLng object of the unparked ParkingSlot, otherwise
	 */
	protected static LatLng unpark(LatLng latLng) {
		// Decreasing in parking lot scale
		for (ParkingLot parkingLot : parkingLots)
			if (parkingLot.contains(latLng))
				parkingLot.occupiedSlots--;

		// Unparking to a ParkingSlot
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				if (parkingSpot.contains(latLng) && parkingSpot.getParked()) {
					parkingSpot.setParked(false);
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

	/**
	 * Returns LatLng's of all of the parked ParkingSlots
	 *
	 * @return LatLng's of all of the parked ParkingSlots
	 */
	protected static ArrayList<LatLng> getParked() {

		return null;
	}

	/**
	 * Sends the app-related complaint to the server.
	 *
	 * @param complaintBody The complaint body.
	 */
	protected static void sendComplaint(String complaintBody) {

	}

	/**
	 * Sends the user-related complaint to the server.
	 *
	 * @param complaintBody  The complaint body.
	 * @param relatedLotName The name of the lot related to the complaint.
	 */
	protected static void sendComplaint(String complaintBody, String relatedLotName) {

	}

	/**
	 * Returns the statistics of the requested lot for the requested time period
	 *
	 * @param lotName    The lot's name.
	 * @param periodType The period type, namely "Daily", "Weekly" or "Monthly".
	 * @return A map mapping occupancy ratios (data) to predetermined String key values.
	 */
	protected static HashMap<String, Double> getStatistics(String lotName, String periodType) {

		return null;
	}

	/**
	 * Returns the concurrent occupancy data in a HashMap with given keys.
	 *
	 * @return new HashMap<String, Double> =
	 *            {
	 *               mescidLotTag  : double
	 *               nanotamLotTag : double
	 *               unamLotTag    : double
	 *            }
	 */
	protected static HashMap<String, Double> getOccupancy() {
		statisticsReference.child("concurrent")
				.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				occupancyData = dataSnapshot.getValue(HashMap.class);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
		return occupancyData;
	}

	/**
	 * Initializer.
	 *
	 * @param parkingLots : All "ParkingLot"s to be added to this class
	 * @param parkingRows : All "ParkingRow"s to be added to this class
	 */
	private static void initLotsAndRows(ParkingLot[] parkingLots, ParkingRow[] parkingRows) {
		ServerUtil.parkingLots = parkingLots;
		ServerUtil.parkingRows = parkingRows;
		for (ParkingLot parkingLot : parkingLots) {
			totalSpots += parkingLot.totalSlots;
		}
		occupiedSpots = 0;

		initialized = true;
	}

	/**
	 * Returns the time formatted in a standardized way; "yyyy-MM-dd--HH-mm-ss", as for GB locale.
	 *
	 * @return The formatted string for current time
	 */
	private static String getTime() {
		return new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss", Locale.ENGLISH)
				.format(Calendar.getInstance().getTime());
	}
}
