package com.shixun.android.leaving_detection.DataCollection;

/**
 * Created by shixunliu on 8/5/17.
 */

public class MagneticHistory {


    private double variance;

    //创建 SingleObject 的一个对象
    private static MagneticHistory instance = new MagneticHistory();

    //让构造函数为 private，这样该类就不会被实例化
    private MagneticHistory(){}

    //获取唯一可用的对象
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
