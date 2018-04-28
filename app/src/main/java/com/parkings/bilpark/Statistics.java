package com.parkings.bilpark;

/**
 * Created by furkan on 28.04.2018
 */
public class Statistics {

    //methods
    public LotStatistics getLot(ParkingLot lot) {
        return new LotStatistics(lot);
    }
}
