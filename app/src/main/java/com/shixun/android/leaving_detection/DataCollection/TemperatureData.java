package com.shixun.android.leaving_detection.DataCollection;

import org.litepal.crud.DataSupport;

/**
 * Created by shixunliu on 15/5/17.
 * The class is used for storing Temperature value in database
 */

public class TemperatureData extends DataSupport {
    private double temperature;
    private double meanOfTemperature;
    private int id;

    public double getemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getMeanOfTemperature() {
        return meanOfTemperature;
    }

    public void setMeanOfTemperature(double meanOfTemperature) {
        this.meanOfTemperature = meanOfTemperature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
