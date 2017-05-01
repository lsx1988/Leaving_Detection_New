package com.shixun.android.leaving_detection.Detection;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by shixunliu on 22/4/17.
 */

public class PressureProcessRunnable implements Runnable {

    private static final String TAG = "Pressure";
    private List<Double> pressureDataList;
    private int dataSize = 10;
    private List<String> str;

    public PressureProcessRunnable(List<Double> dataList, List<String> str) {
        pressureDataList = dataList;
        this.str = str;
    }

    @Override
    public void run() {
        PressureData pressureData = new PressureData();

        double sum = 0;
        for (Double data : pressureDataList) {
            sum += data.doubleValue();
        }

        pressureData.setPressure(sum / pressureDataList.size());

        pressureData.saveThrows();

        pressureDataList.clear();

        if (DataSupport.count(PressureData.class) >= dataSize) {

            str.add(" 1:" + getMean("pressure", PressureData.class)
                        + " 2:" + getMax("pressure")
                        + " 3:" + getMin("pressure")
                        + " 4:" + getVar("pressure")
                        + " 5:" + getEnergy("pressure")
                        + " 6:" + getDiff("pressure")
                        + " 7:" + getHarr_1("pressure")[0]
                        + " 8:" + getHarr_1("pressure")[1]
                        + " 9:" + getHarr_1("pressure")[2]
                        + " 10:" + getHarr_1("pressure")[3]
                        + " 11:" + getHarr_1("pressure")[4]
                        + " 12:" + getHarr_2("pressure")[0]
                        + " 13:" + getHarr_2("pressure")[1]
                        + " 14:" + getHarr_2("pressure")[2]
                        + " 15:" + getHarr_2("pressure")[3]
                        + " 16:" + getHarr_2("pressure")[4]
                        + " 17:" + getHarr_2("pressure")[5]);
            //删除一组数据
            int id = DataSupport.findFirst(PressureData.class).getId();
            DataSupport.delete(PressureData.class, id);
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

    private double getMax(String col) {
        return DataSupport.max(PressureData.class, col, double.class);
    }

    private double getMin(String col) {
        return DataSupport.min(PressureData.class, col, double.class);
    }

    private double getVar(String col) {
        double mean = getMean(col, PressureData.class);
        double count = DataSupport.count(PressureData.class);

        List<PressureData> temp = DataSupport.select(col).find(PressureData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getPressure(),2);
        }

        return (sum - count * Math.pow(mean,2)) / (count - 1);
    }

    private double getEnergy(String col) {
        double count = DataSupport.count(PressureData.class);
        List<PressureData> temp = DataSupport.select(col).find(PressureData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getPressure(),2);
        }
        return sum / count;
    }

    private double getDiff(String col) {
        double first = DataSupport.findFirst(PressureData.class).getPressure();
        double last = DataSupport.select(col).order("id desc").limit(1).find(PressureData.class).get(0).getPressure();
        return Math.abs(last-first);
    }

    private double[] getHarr_1 (String col) {
        double[] result = new double[5];
        int[] step = {1,3,5,7,9};
        double sumFirst = 0;
        double sumLast = 0;
        for(int i = 0; i < step.length; i++) {
            List<PressureData> firstSet = DataSupport.select(col).order("id asc").limit(step[i]).find(PressureData.class);
            for(PressureData data: firstSet){
                sumFirst += data.getPressure();
            }

            List<PressureData> lastSet = DataSupport.select(col).order("id desc").limit(step[i]).find(PressureData.class);
            for(PressureData data: lastSet){
                sumLast += data.getPressure();
            }

            result[i] = sumLast - sumFirst;
        }

        return result;
    }

    private double[] getHarr_2 (String col) {
        int count = DataSupport.count(PressureData.class);
        double[] result = new double[6];
        int[] step = {2,4,6,8,10,12};
        double sumFirst = 0;
        double sumLast = 0;
        for(int i = 0; i < step.length; i++) {

            int num = count / step[i];

            List<PressureData> firstSet = DataSupport.select(col).order("id asc").limit(num).find(PressureData.class);
            for(PressureData data: firstSet){
                sumFirst += data.getPressure();
            }

            List<PressureData> lastSet = DataSupport.select(col).order("id desc").limit(num).find(PressureData.class);
            for(PressureData data: lastSet){
                sumLast += data.getPressure();
            }

            result[i] = sumLast - sumFirst;
        }

        return result;
    }
}
