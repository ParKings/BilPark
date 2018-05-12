package com.parkings.bilpark;

import android.support.annotation.NonNull;

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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p><b>Singleton class.</b></p>
 * <p>
 * Includes methods providing the app with connectivity to the Firebase services.
 * Created on 2018.04.20 by Emre Acarturk.
 * ToDo: Listener for getParkingSpots() method
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
	private final DatabaseReference statisticsReference;
	private final DatabaseReference parkingDataReference;
	private final DatabaseReference complaintsReference;
	private final int noOfSlots;

	private static final String statisticsTag = "statistics";
	private static final String parkingDataTag = "parkingdata";
	private static final String complaintsTag = "complaints";

	public static final String nanotamLotTag = "Nanotam";
	public static final String unamLotTag = "Unam";
	public static final String mescidLotTag = "Mescid";

	// Static checker
	private static ServerUtil serverUtil;

	// Properties
	private ArrayList<ParkingLot> parkingLots = new ArrayList<ParkingLot>();
	private ArrayList<ParkingRow> parkingRows = new ArrayList<ParkingRow>();
	private ConcurrentHashMap<String, Double> occupancyData;
	private ConcurrentHashMap<String, Double> statisticsData;
	private CopyOnWriteArrayList<ParkingSpot> slots;

	// Constructor

	/**
	 * Default constructor
	 */
	private ServerUtil() {
		// The method "initLotsAndRows" is not yet called
		occupancyData = new ConcurrentHashMap<>();
		statisticsData = new ConcurrentHashMap<>();

		// Database and reference object initializations
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference rootReference = firebaseDatabase.getReference();

		parkingLots.add(new ParkingLot(148, new LatLng[]{new LatLng(39.86643115675040, 32.74708114564418),
				new LatLng(39.86654052536382, 32.74778019636869),
				new LatLng(39.86715298637697, 32.74763870984316),
				new LatLng(39.86722426824893, 32.74693094193936)}, nanotamLotTag));
		parkingRows.add(new ParkingRow(36, new LatLng[]{new LatLng(39.867141, 32.747056),
				new LatLng(39.866530, 32.747152),
				new LatLng(39.866542, 32.747316),
				new LatLng(39.867104, 32.747219)}));

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
				i++;
			}
		}
		noOfSlots = i;

		// Parked slot data retrieval listener.
		parkingDataReference.child("slots")
				.orderByChild(ParkingSpot.isParkedTag)
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						slots = new CopyOnWriteArrayList<>();
						for (int i = 0; i > noOfSlots; i++)
							slots.add(dataSnapshot.child(i + "").getValue(ParkingSpot.class));
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
					}
				});
	}

	/**
	 * Get instance method.
	 *
	 * @return An instance of this singleton.
	 */
	public static ServerUtil getInstance() {
		if (serverUtil != null)
			return serverUtil;

		serverUtil = new ServerUtil();
		return serverUtil;
	}

	// Methods

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
	LatLng park(@NonNull LatLng latLng) {
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
	LatLng unpark(@NonNull LatLng latLng) {
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
	CopyOnWriteArrayList<ParkingSpot> getParkingSpots() {
		return slots;
	}

	/**
	 * Returns all ParkingLots
	 *
	 * @return all ParkingLots
	 */
	ParkingLot[] getParkingLots() {
		return (ParkingLot[]) parkingLots.toArray();
	}

	/**
	 * Sends the app-related complaint to the server.
	 *
	 * @param complaintBody The complaint body.
	 */
	void sendComplaint(@NonNull String complaintBody) {
		complaintsReference.child("apprelated").push().setValue(complaintBody);
	}

	/**
	 * Sends the user-related complaint to the server.
	 *
	 * @param complaintBody  The complaint body.
	 * @param relatedLotName The name of the lot related to the complaint.
	 */
	void sendComplaint(@NonNull String complaintBody, @NonNull String relatedLotName) {
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
	ConcurrentHashMap<String, Double> getStatistics(@NonNull String lotName, @NonNull String periodType) {
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
	 * {
	 * mescidLotTag  : double
	 * nanotamLotTag : double
	 * unamLotTag    : double
	 * }
	 */
	ConcurrentHashMap<String, Double> getOccupancy() {
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
	 * Returns the time formatted in a standardized way; "yyyy-MM-dd--HH-mm-ss", as for GB locale.
	 *
	 * @return The formatted string for current time
	 */
	@SuppressWarnings("unused")
	// Yeah, we know, thou hadn't used yet.
	String getTime() {
		return new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss", Locale.ENGLISH)
				.format(Calendar.getInstance().getTime());
	}
}
