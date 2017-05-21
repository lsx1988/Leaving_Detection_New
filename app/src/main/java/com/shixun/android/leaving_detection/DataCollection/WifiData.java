package com.shixun.android.leaving_detection.DataCollection;

import org.litepal.crud.DataSupport;

/**
 * Created by shixunliu on 13/3/17.
 */

public class WifiData extends DataSupport{

    private int id;

    private Double homeWifiLevel;

    //private Double meanOfAllWifiLevel;

    //private Double stdOfAllWifiLevel;

    private Double meanOfHomeWifi;

    public Double getHomeWifiLevel() {
        return homeWifiLevel;
    }

    public void setHomeWifiLevel(Double homeWifiLevel) {
        this.homeWifiLevel = homeWifiLevel;
    }

//    public Double getMeanOfAllWifiLevel() {
//        return meanOfAllWifiLevel;
//    }
//
//    public void setMeanOfAllWifiLevel(Double meanOfAllWifiLevel) {
//        this.meanOfAllWifiLevel = meanOfAllWifiLevel;
//    }
//
//    public Double getStdOfAllWifiLevel() {
//        return stdOfAllWifiLevel;
//    }
//
//    public void setStdOfAllWifiLevel(Double stdOfAllWifiLevel) {
//        this.stdOfAllWifiLevel = stdOfAllWifiLevel;
//    }

    public Double getMeanOfHomeWifi() {
        return meanOfHomeWifi;
    }

    public void setMeanOfHomeWifi(Double meanOfHomeWifi) {
        this.meanOfHomeWifi = meanOfHomeWifi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
