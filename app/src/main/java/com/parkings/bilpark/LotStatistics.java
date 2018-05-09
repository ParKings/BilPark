package com.parkings.bilpark;

import java.util.concurrent.ConcurrentHashMap;

/**
 * LotStatistics class
 * which obtains daily, weekly and monthly
 * information about the specified parking lot.
 *
 * Created by furkan on 28.04.2018
 *
 * @author furkan
 * @version 2018.04.28.0
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
