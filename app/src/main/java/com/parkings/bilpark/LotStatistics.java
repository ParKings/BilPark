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
    private final ServerUtil serverUtil;
    private String lotName;

    //constructor

    /**
     * Default constructor
     *
     * @param lotName
     */
    public LotStatistics(String lotName) {
        this.lotName = lotName;
        serverUtil = ServerUtil.getInstance();
    }

    //methods

    /**
     * Provides the type of schedules for
     * DetailedStatisticsFragment class
     *
     * @return String[]
     */
    public String[] getScheduleTypes() {
        return new String[] {DAILY, WEEKLY, MONTHLY};
    }

    /**
     * Receives daily statistics of particular parking lot
     * and obtains such statistics from the server
     *
     * @return ConcurrentHashMap<String, Double>
     */
    public ConcurrentHashMap<String, Double> getDaily() {
        return serverUtil.getStatistics(lotName, DAILY);
    }

    /**
     * Receives weekly statistics of particular parking lot
     * and obtains such statistics from the server
     *
     * @return ConcurrentHashMap<String, Double>
     */
    public ConcurrentHashMap<String, Double> getWeekly() {
        return serverUtil.getStatistics(lotName, WEEKLY);
    }

    /**
     * Receives monthly statistics of particular parking lot
     * and obtains such statistics from the server
     *
     * @return ConcurrentHashMap<String, Double>
     */
    public ConcurrentHashMap<String, Double> getMonthly() {
        return serverUtil.getStatistics(lotName, MONTHLY);
    }
}
