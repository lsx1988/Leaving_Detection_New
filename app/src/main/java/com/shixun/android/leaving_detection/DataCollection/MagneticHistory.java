package com.shixun.android.leaving_detection.DataCollection;

/**
 * Created by shixunliu on 8/5/17.
 * The singlton class used for storing magnetic variance
 */

public class MagneticHistory {

    private double variance;

    private static MagneticHistory instance = new MagneticHistory();

    private MagneticHistory(){}

    public static MagneticHistory getInstance(){
        return instance;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }
}
