package com.parkings.bilpark;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by furkan on 28.04.2018
 */
public class LotStatistics {

    //properties
    private final String DAILY = "Daily";
    private final String WEEKLY = "Weekly";
    private final String MONTHLY = "monthly";
    private ParkingLot lot;

    //constructor
    public LotStatistics(ParkingLot lot) {
        this.lot = lot;
    }

    //methods
    public ConcurrentHashMap<String, Double> getDaily() {
        return ServerUtil.getStatistics(lot.getName(), DAILY);
    }

    public ConcurrentHashMap<String, Double> getWeekly() {
        return ServerUtil.getStatistics(lot.getName(), WEEKLY);
    }

    public ConcurrentHashMap<String, Double> getMonthly() {
        return ServerUtil.getStatistics(lot.getName(), MONTHLY);
    }
}
