package com.parkings.bilpark;

/**
 * A class for holding general complaint format
 *
 * @author Emre Acarturk
 * @version 2018.04.25.0
 */
public class Complaint {
	private String complaintBody;
	private String relatedLotName;

	public Complaint() {
		// Needed for Firebase integration
	}

	public Complaint(String complaintBody, String relatedLotName) {
		this.complaintBody = complaintBody;
		this.relatedLotName = relatedLotName;
	}
}
