package com.shixun.android.leaving_detection.DataCollection;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by shixunliu on 15/5/17.
 */

public class TemperatureProcessRunnable implements Runnable{

    private static final String TAG = "Temperature";
    private List<Double> temperatureDataList;
    private int dataSize = 20;
    private String[] strArray;
    private double[] doubleArray;

    public TemperatureProcessRunnable(List<Double> dataList, String[] strArray, double[] doubleArray) {
        this.temperatureDataList = dataList;
        this.strArray = strArray;
        this.doubleArray = doubleArray;
    }

    @Override
    public void run() {
        TemperatureData sample = new TemperatureData();

        double sum = 0;
        for (Double data : temperatureDataList) {
            sum += data.doubleValue();
        }

        double currentTem = sum / temperatureDataList.size();

        sample.setTemperature(currentTem);

        if(DataSupport.count(TemperatureData.class) == 0){
            sample.setMeanOfTemperature(currentTem);
        } else {
            sample.setMeanOfTemperature(getMean("temperature", TemperatureData.class));
        }

        sample.saveThrows();

        temperatureDataList.clear();

        if (DataSupport.count(TemperatureData.class) >= dataSize) {

            double currentVar = getVar("meanOfTemperature");

            doubleArray[3] = getMean("temperature", TemperatureData.class);
            strArray[3] = " 70:" + getMean("temperature", TemperatureData.class)
                    + " 71:" + currentVar
                    + " 72:" + getEnergy("meanOfTemperature")
                    + " 73:" + getDiff("meanOfTemperature");

            int id = DataSupport.findFirst(TemperatureData.class).getId();
            DataSupport.delete(TemperatureData.class, id);

        }
    }

    private double getMean(String col, Class className) {
        double mean = DataSupport.average(className, col);
        return mean;
    }

    private double getVar(String col) {
        double mean = getMean(col, TemperatureData.class);
        double count = DataSupport.count(TemperatureData.class);

        List<TemperatureData> temp = DataSupport.select(col).find(TemperatureData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getMeanOfTemperature(),2);
        }

        return (sum - count * Math.pow(mean,2)) / (count - 1);
    }

    private double getEnergy(String col) {
        double count = DataSupport.count(TemperatureData.class);
        List<TemperatureData> temp = DataSupport.select(col).find(TemperatureData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getMeanOfTemperature(),2);
        }
        return sum / count;
    }

    private double getDiff(String col) {

        List<TemperatureData> lastSet;
        lastSet = DataSupport.select(col).order("id desc").limit(5).find(TemperatureData.class);

        double max = lastSet.get(0).getMeanOfTemperature();
        double min = lastSet.get(4).getMeanOfTemperature();

        return max - min;
    }
}
