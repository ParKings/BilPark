package com.parkings.bilpark;

/**
 * Created by furkan on 28.04.2018
 */
public class Statistics {

    //methods

    /**
     * Receives the information of particular parking lot
     * so as to obtain the time statistics of that parking lot
     *
     * @param lot
     * @return LotStatistics
     */
    public LotStatistics getLot(ParkingLot lot) {
        return new LotStatistics(lot);
    }
}
