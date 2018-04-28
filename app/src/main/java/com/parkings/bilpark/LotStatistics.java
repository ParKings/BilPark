package com.parkings.bilpark;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by furkan on 28.04.2018
 */
public class LotStatistics {

    //properties
    private final String DAILY = "Daily";
    private final String WEEKLY = "Weekly";
    private final String MONTHLY = "Monthly";
    private ParkingLot lot;

    //constructor

    /**
     * Default constructor
     *
     * @param lot
     */
    public LotStatistics(ParkingLot lot) {
        this.lot = lot;
    }

    //methods
    /**
     * Receives daily statistics of particular parking lot
     * and obtains such statistics from the server
     *
     * @return ConcurrentHashMap<String, Double>
     */
    public ConcurrentHashMap<String, Double> getDaily() {
        return ServerUtil.getStatistics(lot.getName(), DAILY);
    }

    /**
     * Receives weekly statistics of particular parking lot
     * and obtains such statistics from the server
     *
     * @return ConcurrentHashMap<String, Double>
     */
    public ConcurrentHashMap<String, Double> getWeekly() {
        return ServerUtil.getStatistics(lot.getName(), WEEKLY);
    }

    /**
     * Receives monthly statistics of particular parking lot
     * and obtains such statistics from the server
     *
     * @return ConcurrentHashMap<String, Double>
     */
    public ConcurrentHashMap<String, Double> getMonthly() {
        return ServerUtil.getStatistics(lot.getName(), MONTHLY);
    }
}
