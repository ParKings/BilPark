package com.parkings.bilpark;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Includes methods providing the app with connectivity to the Firebase services.
 *
 * @author Emre Acarturk
 * @version 2018.04.20.0
 */
public class ServerUtil {

	private static DatabaseReference statisticsReference;
	private static DatabaseReference parkingDataReference;

	public static final String statisticsTag  = "statistics";
	public static final String parkingDataTag = "parkingData";

	public static final String nanotamLotTag = "nanotam";
	public static final String unamLotTag = "unam";
	public static final String mescidLotTag = "mescid";

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

	// Static initializer of the class, instantiates required objects
	static {
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference rootReference = firebaseDatabase.getReference();

		statisticsReference = rootReference.child(statisticsTag);
		parkingDataReference = rootReference.child(parkingDataTag);

		statisticsReference.child(nanotamLotTag);
		statisticsReference.child(unamLotTag);
		statisticsReference.child(mescidLotTag);

		;
	}

	/**
	 * Returns the time formatted in a standardized way; "yyyy-MM-dd--HH-mm-ss", as for GB locale.
	 * @return The formatted string for current time
	 */
	private static String getTime() {
		return new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss", Locale.ENGLISH)
				.format(Calendar.getInstance().getTime());
	}

	/**
	 * Parks the car and sends the parking data to the database.
	 * <p>
	 * <b>Note:</b> If the requested ParkingSlot is already occupied tries alternative paths and if
	 * all fail, registers the car to the ParkingLot itself.
	 * </p>
	 *
	 * @param latLng The coordinates to park the car. This value does not need to be equal to any
	 *               specific parking slot's coordinate, but rather, the according lot is found by
	 *               the algorithm.
	 */
	protected static void park(LatLng latLng) {

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
	protected static Map<String, Double> getStatistics(String lotName, String periodType) {

		return null;
	}
}
