package com.parkings.bilpark;

/**
 * A class for holding general complaint format
 *
 * @author Emre Acarturk
 * @version 2018.04.28.0 : Added getters and JavaDoc
 *
 */
public class Complaint {
	private String complaintBody;
	private String relatedLotName;

	/**
	 * Default constructor needed for Firebase integration
	 */
	public Complaint() {
	}

	/**
	 * Secondary constructor initializing parameters.
	 *
	 * @param complaintBody  The body part of the complaint
	 * @param relatedLotName The related lot's name, in String format
	 */
	public Complaint(String complaintBody, String relatedLotName) {
		this.complaintBody = complaintBody;
		this.relatedLotName = relatedLotName;
	}

	/**
	 * Returns the complaint body part of the complaint.
	 *
	 * @return The body part of the complaint
	 */
	public String getComplaintBody() {
		return complaintBody;
	}

	/**
	 * Returns the related lot name for the complaint.
	 *
	 * @return The related lot name
	 */
	public String getRelatedLotName() {
		return relatedLotName;
	}
}
