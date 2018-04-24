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
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Includes methods providing the app with connectivity to the Firebase services.
 * Created on 2018.04.20 by Emre Acarturk.
 * ToDo: Everything except park() and unpark() methods are connected to the server.
 * </p>
 * <p>
 * <b>Implementation note:</b> ArrayList used might not be thread-safe. Find a better alternative.
 * </p>
 * <p>
 * Includes code written by Uğur Yılmaz on 15.04.2018 (Park method and the constructor).
 * </p>
 * @author Emre Acarturk
 * @version 2018.04.25.0
 */
public class ServerUtil {

	// Constants
	private static final DatabaseReference statisticsReference;
	private static final DatabaseReference parkingDataReference;
	private static final DatabaseReference complaintsReference;

	private static final String statisticsTag  = "statistics";
	private static final String parkingDataTag = "parkingdata";
	private static final String complaintsTag  = "complaints";

	public static final String nanotamLotTag = "nanotam";
	public static final String unamLotTag    = "unam";
	public static final String mescidLotTag  = "mescid";

	//properties
	private static boolean initialized;
	private static ParkingLot[] parkingLots;
	private static ParkingRow[] parkingRows;
	private static ConcurrentHashMap<String, Double> occupancyData;
	private static ConcurrentHashMap<String, Double> statisticsData;
	private static ArrayList<ParkingSpot> parkedSlots;
	private static int totalSpots = 0;
	private static int occupiedSpots;

	// Static initializer
	static {
		// The method "initLotsAndRows" is not yet called
		initialized = false;
		occupancyData = new ConcurrentHashMap<>();
		statisticsData = new ConcurrentHashMap<>();

		// Database and reference object initializations
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference rootReference = firebaseDatabase.getReference();

		// ToDo: Build the necessary lots & rows. Then add them.
		initLotsAndRows(null, null);

		// Main children, from root
		statisticsReference = rootReference.child(statisticsTag);
		parkingDataReference = rootReference.child(parkingDataTag);
		complaintsReference = rootReference.child(complaintsTag);

		// Statistics' children; lots
		statisticsReference.child(nanotamLotTag);
		statisticsReference.child(unamLotTag);
		statisticsReference.child(mescidLotTag);

		// Parked slot data retrieval listener.
		parkedSlots = new ArrayList<>();
		parkingDataReference.child("slots")
				.orderByChild(ParkingSpot.isParkedTag).equalTo(true) // Those slots that are parked
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						// ToDo: Write the following retrieval line thoroughly. Currently wrong.
						parkedSlots = dataSnapshot.getValue(ArrayList.class);
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});

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
					return parkingSpot.getCenter();
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

		// Unparking from a ParkingSlot
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				if (parkingSpot.contains(latLng) && parkingSpot.getParked()) {
					parkingSpot.setParked(false);
					return parkingSpot.getCenter();
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
	protected static ArrayList<ParkingSpot> getParked() {
		return parkedSlots;
	}

	/**
	 * Sends the app-related complaint to the server.
	 *
	 * @param complaintBody The complaint body.
	 */
	protected static void sendComplaint(String complaintBody) {
		complaintsReference.child("apprelated").push().setValue(complaintBody);
	}

	/**
	 * Sends the user-related complaint to the server.
	 *
	 * @param complaintBody  The complaint body.
	 * @param relatedLotName The name of the lot related to the complaint.
	 */
	protected static void sendComplaint(String complaintBody, String relatedLotName) {
		complaintsReference.child("userrelated").push()
				.setValue(new Complaint(complaintBody, relatedLotName));
	}

	/**
	 * Returns the statistics of the requested lot for the requested time period
	 *
	 * @param lotName    The lot's name.
	 * @param periodType The period type, namely "Daily", "Weekly" or "Monthly".
	 * @return A map mapping occupancy ratios (data) to predetermined String key values.
	 */
	protected static ConcurrentHashMap<String, Double> getStatistics(String lotName, String periodType) {
		statisticsReference.child(lotName).child(periodType)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						statisticsData = dataSnapshot.getValue(ConcurrentHashMap.class);
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});
		return statisticsData;
	}

	/**
	 * Returns the concurrent occupancy data in a ConcurrentHashMap with given keys.
	 *
	 * @return new ConcurrentHashMap<String, Double> =
	 *            {
	 *               mescidLotTag  : double
	 *               nanotamLotTag : double
	 *               unamLotTag    : double
	 *            }
	 */
	protected static ConcurrentHashMap<String, Double> getOccupancy() {
		statisticsReference.child("concurrent")
				.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				occupancyData = dataSnapshot.getValue(ConcurrentHashMap.class);
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
