package com.parkings.bilpark;

import android.support.annotation.NonNull;
import android.util.Log;

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
 * ToDo: Listener for getParked() method
 * ToDo: Initializer of 'ParkingRow's and 'ParkingLot's (initLotsAndRows()).
 * </p>
 * <p>
 * <b>Implementation note:</b> ArrayList used might not be thread-safe. Find a better alternative.
 * </p>
 * <p>
 * Includes code written by Uğur Yılmaz on 15.04.2018 (Park method and the constructor).
 * </p>
 *
 * @author Emre Acarturk
 * @version 2018.04.25.0
 */
public class ServerUtil {

	// Constants
	private static final DatabaseReference statisticsReference;
	private static final DatabaseReference parkingDataReference;
	private static final DatabaseReference complaintsReference;
	private static final int noOfSlots;

	private static final String statisticsTag  = "statistics";
	private static final String parkingDataTag = "parkingdata";
	private static final String complaintsTag  = "complaints";

	public static final String nanotamLotTag = "Nanotam";
	public static final String unamLotTag    = "Unam";
	public static final String mescidLotTag  = "Mescid";

	// Properties
	private static ParkingLot[] parkingLots;
	private static ParkingRow[] parkingRows;
	private static ConcurrentHashMap<String, Double> occupancyData;
	private static ConcurrentHashMap<String, Double> statisticsData;
	private static ArrayList<ParkingSpot> parkedSlots;

	// Static initializer
	static {
		// The method "initLotsAndRows" is not yet called
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

		// Serializing ParkingSlot variables in the database
		int i = 0;
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				parkingDataReference.child("slots").child(i + "").setValue(parkingSpot);
			}
			i++;
		}
		noOfSlots = i;

		// Parked slot data retrieval listener.
		parkingDataReference.child("slots")
				.orderByChild(ParkingSpot.isParkedTag).equalTo(true) // Those slots that are parked
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						parkedSlots = new ArrayList<>();
						for (int i = 0; i > noOfSlots; i++)
							parkedSlots.add(dataSnapshot.child(i + "").getValue(ParkingSpot.class));
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
	protected static LatLng park(@NonNull LatLng latLng) {
		// Incrementing in parking lot scale
		for (ParkingLot parkingLot : parkingLots) {
			if (parkingLot.contains(latLng) && !parkingLot.isFull()) {
				parkingLot.occupiedSlots++;
				parkingDataReference.child("lots").setValue(parkingLot);
			}
		}
		// Parking to a ParkingSlot
		int i = 0;
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				if (parkingSpot.contains(latLng) && !parkingSpot.isParked()) {
					parkingSpot.park();
					parkingDataReference.child("slots").child(i + "").setValue(parkingSpot);
					return parkingSpot.getCenter();
				}
				i++;
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
	protected static LatLng unpark(@NonNull LatLng latLng) {
		// Decreasing in parking lot scale
		for (ParkingLot parkingLot : parkingLots) {
			if (parkingLot.contains(latLng) && !parkingLot.isEmpty()) {
				parkingLot.occupiedSlots--;
				parkingDataReference.child("lots").setValue(parkingLot);
			}
		}
		// Unparking from a ParkingSlot
		int i = 0;
		for (ParkingRow parkingRow : parkingRows) {
			for (ParkingSpot parkingSpot : parkingRow.parkingSpots) {
				if (parkingSpot.contains(latLng) && parkingSpot.isParked()) {
					parkingSpot.unpark();
					parkingDataReference.child("slots").child(i + "").setValue(parkingSpot);
					return parkingSpot.getCenter();
				}
				i++;
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
	 * Returns all ParkingLots
	 *
	 * @return all ParkingLots
	 */
	protected static ParkingLot[] getParkingLots() { return parkingLots; }

	/**
	 * Sends the app-related complaint to the server.
	 *
	 * @param complaintBody The complaint body.
	 */
	protected static void sendComplaint(@NonNull String complaintBody) {
		complaintsReference.child("apprelated").push().setValue(complaintBody);
	}

	/**
	 * Sends the user-related complaint to the server.
	 *
	 * @param complaintBody  The complaint body.
	 * @param relatedLotName The name of the lot related to the complaint.
	 */
	protected static void sendComplaint(@NonNull String complaintBody, @NonNull String relatedLotName) {
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
	protected static ConcurrentHashMap<String, Double> getStatistics(@NonNull String lotName, @NonNull String periodType) {
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
		// ToDo: Special case: null && null
		if (parkingLots == null && parkingRows == null) {
			ServerUtil.parkingLots = new ParkingLot[1];
			ServerUtil.parkingLots[0] = new ParkingLot(1, new LatLng[0], nanotamLotTag);
			ServerUtil.parkingRows = new ParkingRow[1];
			ServerUtil.parkingRows[0] = new ParkingRow(0, new LatLng[0]);

			Log.i("CHECK_FOR_NPE", ServerUtil.parkingLots + " & " + ServerUtil.parkingRows);
		} else {
			ServerUtil.parkingLots = parkingLots;
			ServerUtil.parkingRows = parkingRows;
		}
	}

	/**
	 * Returns the time formatted in a standardized way; "yyyy-MM-dd--HH-mm-ss", as for GB locale.
	 *
	 * @return The formatted string for current time
	 */
	@SuppressWarnings("unused") // Yeah, we know, thou hadn't used yet.
	private static String getTime() {
		return new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss", Locale.ENGLISH)
				.format(Calendar.getInstance().getTime());
	}
}
