package com.shixun.android.leaving_detection.DataCollection;

import org.litepal.crud.DataSupport;

/**
 * Created by shixunliu on 26/3/17.
 * The class is used for storing pressure value in database
 */

public class PressureData extends DataSupport {

    private double pressure;
    private double meanOfPressure;
    private int id;

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getMeanOfPressure() {
        return meanOfPressure;
    }

    public void setMeanOfPressure(double meanOfPressure) {
        this.meanOfPressure = meanOfPressure;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
