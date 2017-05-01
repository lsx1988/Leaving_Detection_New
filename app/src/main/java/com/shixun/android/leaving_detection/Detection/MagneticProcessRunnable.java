package com.shixun.android.leaving_detection.Detection;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by shixunliu on 23/4/17.
 */

public class MagneticProcessRunnable implements Runnable{

    private static final String TAG = "Magnetic";
    private List<Double> magneticDataList;
    private int dataSize = 10;
    private String[] strArray;

    public MagneticProcessRunnable(List<Double> dataList, String[] strArray) {
        magneticDataList = dataList;
        this.strArray = strArray;
    }

    @Override
    public void run() {
        MagneticData magneticData = new MagneticData();

        double sum = 0;
        for (Double data : magneticDataList) {
            sum += data.doubleValue();
        }

        magneticData.setMagnetic(sum / magneticDataList.size());

        magneticData.saveThrows();

        magneticDataList.clear();

        if (DataSupport.count(MagneticData.class) >= dataSize) {

            strArray[2] = (" 24:" + getMean("magnetic", MagneticData.class)
                    + " 25:" + getVar("magnetic")
                    + " 26:" + getEnergy("magnetic"))
                    + " 27:" + getDiff("magnetic");
            //删除一组数据
            int id = DataSupport.findFirst(MagneticData.class).getId();
            DataSupport.delete(MagneticData.class, id);
        }
    }

    /**
     *
     * @param col
     * @param className
     * @return
     */

    private double getMean(String col, Class className) {
        double mean = DataSupport.average(className, col);
        return mean;
    }

    private double getVar(String col) {
        double mean = getMean(col, MagneticData.class);
        double count = DataSupport.count(MagneticData.class);

        List<MagneticData> temp = DataSupport.select(col).find(MagneticData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getMagnetic(),2);
        }

        return (sum - count * Math.pow(mean,2)) / (count - 1);
    }

    private double getEnergy(String col) {
        double count = DataSupport.count(MagneticData.class);
        List<MagneticData> temp = DataSupport.select(col).find(MagneticData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getMagnetic(),2);
        }
        return sum / count;
    }

    private double getDiff(String col) {
        double first = DataSupport.findFirst(MagneticData.class).getMagnetic();
        double last = DataSupport.select(col).order("id desc").limit(1).find(MagneticData.class).get(0).getMagnetic();
        return Math.abs(last-first);
    }
}
