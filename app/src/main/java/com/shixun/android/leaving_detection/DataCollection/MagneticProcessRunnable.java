package com.shixun.android.leaving_detection.DataCollection;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by shixunliu on 23/4/17.
 */

public class MagneticProcessRunnable implements Runnable{

    private static final String TAG = "Magnetic";
    private List<Double> magneticDataList;
    private int dataSize = 20;
    private String[] strArray;
    private double lastVar;
    private double varPercentage;

    public MagneticProcessRunnable(List<Double> dataList, String[] strArray) {
        magneticDataList = dataList;
        this.strArray = strArray;
    }

    @Override
    public void run() {
        MagneticData sample = new MagneticData();

        double sum = 0;
        for (Double data : magneticDataList) {
            sum += data.doubleValue();
        }

        double currentMag = sum / magneticDataList.size();

        sample.setMagnetic(currentMag);

        if(DataSupport.count(MagneticData.class) == 0){
            sample.setMeanOfMagnetic(currentMag);
        } else {
            sample.setMeanOfMagnetic(getMean("magnetic", MagneticData.class));
        }

        sample.saveThrows();

        magneticDataList.clear();

        if (DataSupport.count(MagneticData.class) >= dataSize) {

            double currentVar = getVar("meanOfMagnetic");
            lastVar = MagneticHistory.getInstance().getVariance();

            if(lastVar == 0) {
                varPercentage = 0;
            } else {
                varPercentage = Math.abs(currentVar - lastVar);
            }

            strArray[2] = " 60:" + getMean("magnetic", MagneticData.class)
                    + " 61:" + currentVar
                    + " 62:" + getEnergy("meanOfMagnetic")
                    + " 63:" + getDiff("meanOfMagnetic")
                    + " 64:" + varPercentage;
            //删除一组数据
            int id = DataSupport.findFirst(MagneticData.class).getId();
            DataSupport.delete(MagneticData.class, id);

            MagneticHistory.getInstance().setVariance(currentVar);
        }
    }

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
            sum += Math.pow(temp.get(i).getMeanOfMagnetic(),2);
        }

        return (sum - count * Math.pow(mean,2)) / (count - 1);
    }

    private double getEnergy(String col) {
        double count = DataSupport.count(MagneticData.class);
        List<MagneticData> temp = DataSupport.select(col).find(MagneticData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getMeanOfMagnetic(),2);
        }
        return sum / count;
    }

    private double getDiff(String col) {

        List<MagneticData> lastSet;
        lastSet = DataSupport.select(col).order("id desc").limit(5).find(MagneticData.class);

        double max = lastSet.get(0).getMeanOfMagnetic();
        double min = lastSet.get(4).getMeanOfMagnetic();

        return max - min;
    }
}
