package com.shixun.android.leaving_detection.DataCollection;

import org.litepal.crud.DataSupport;

/**
 * Created by shixunliu on 23/4/17.
 * The class is used for storing magnetic value in database
 */

public class MagneticData extends DataSupport {

    private double magnetic;
    private double meanOfMagnetic;
    private int id;

    public double getMagnetic() {
        return magnetic;
    }

    public void setMagnetic(double magnetic) {
        this.magnetic = magnetic;
    }

    public double getMeanOfMagnetic() {
        return meanOfMagnetic;
    }

    public void setMeanOfMagnetic(double meanOfMagnetic) {
        this.meanOfMagnetic = meanOfMagnetic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
