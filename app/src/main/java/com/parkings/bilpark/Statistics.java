package com.parkings.bilpark;

/**
 * Statistics class
 * which obtains the information about parking lots
 * with respect to particular time intervals.
 *
 * Created by furkan on 28.04.2018
 * Modified by furkan on 05.05.2018
 *
 * @author furkan
 * @version 2018.05.05.0
 */
public class Statistics {

    //methods

    /**
     * Receives the name of particular parking lot
     * so as to obtain the time statistics of that parking lot
     *
     * @param lotName
     * @return LotStatistics
     */
    protected static LotStatistics getLotStatistics(String lotName) {
        return new LotStatistics(lotName);
    }
}
