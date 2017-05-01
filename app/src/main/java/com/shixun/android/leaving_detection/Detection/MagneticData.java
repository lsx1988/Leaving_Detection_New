package com.shixun.android.leaving_detection.Detection;

import org.litepal.crud.DataSupport;

/**
 * Created by shixunliu on 23/4/17.
 */

public class MagneticData extends DataSupport {

    private double magnetic;
    private int id;

    public double getMagnetic() {
        return magnetic;
    }

    public void setMagnetic(double magnetic) {
        this.magnetic = magnetic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
