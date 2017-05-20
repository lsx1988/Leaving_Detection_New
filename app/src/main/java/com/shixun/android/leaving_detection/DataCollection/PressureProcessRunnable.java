package com.shixun.android.leaving_detection.DataCollection;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by shixunliu on 22/4/17.
 */

public class PressureProcessRunnable implements Runnable {

    private static final String TAG = "Pressure";
    private List<Double> pressureDataList;
    private int dataSize = 20;
    private String[] strArray;
    private double[] doubleArray;

    public PressureProcessRunnable(List<Double> dataList,String[] strArray, double[] doubleArray) {
        pressureDataList = dataList;
        this.strArray = strArray;
        this.doubleArray = doubleArray;
    }

    @Override
    public void run() {
        PressureData sample = new PressureData();

        double sum = 0;
        for (Double data : pressureDataList) {
            sum += data.doubleValue();
        }

        double currentPressure = sum / pressureDataList.size();

        sample.setPressure(currentPressure);

        if(DataSupport.count(PressureData.class) == 0) {
            sample.setMeanOfPressure(currentPressure);
        } else {
            sample.setMeanOfPressure(getMean("pressure", PressureData.class));
        }

        sample.saveThrows();

        pressureDataList.clear();

        if (DataSupport.count(PressureData.class) >= dataSize) {

            doubleArray[0] = getMean("pressure", PressureData.class);
            strArray[0] = " 1:" + getMean("pressure", PressureData.class)
                        + " 2:" + getVar("meanOfPressure")
                        + " 3:" + getEnergy("meanOfPressure")
                        + " 4:" + getDiff("meanOfPressure");
//                        + " 2:" + getMax("meanOfPressure")
//                        + " 3:" + getMin("meanOfPressure")
//                        + " 7:" + getHarr_1("meanOfPressure")[0]
//                        + " 8:" + getHarr_1("meanOfPressure")[1]
//                        + " 9:" + getHarr_1("meanOfPressure")[2]
//                        + " 10:" + getHarr_1("meanOfPressure")[3]
//                        + " 11:" + getHarr_1("meanOfPressure")[4]
//                        + " 12:" + getHarr_2("meanOfPressure")[0]
//                        + " 13:" + getHarr_2("meanOfPressure")[1]
//                        + " 14:" + getHarr_2("meanOfPressure")[2]
//                        + " 15:" + getHarr_2("meanOfPressure")[3]
//                        + " 16:" + getHarr_2("meanOfPressure")[4]
//                        + " 17:" + getHarr_2("meanOfPressure")[5]);
            int id = DataSupport.findFirst(PressureData.class).getId();
            DataSupport.delete(PressureData.class, id);
        }
    }

    private double getMean(String col, Class className) {
        double mean = DataSupport.average(className, col);
        return mean;
    }

    private double getVar(String col) {
        double mean = getMean(col, PressureData.class);
        double count = DataSupport.count(PressureData.class);

        List<PressureData> temp = DataSupport.select(col).find(PressureData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getMeanOfPressure(),2);
        }

        return (sum - count * Math.pow(mean,2)) / (count - 1);
    }

    private double getEnergy(String col) {
        double count = DataSupport.count(PressureData.class);
        List<PressureData> temp = DataSupport.select(col).find(PressureData.class);
        double sum = 0;
        for (int i = 0; i < temp.size(); i++) {
            sum += Math.pow(temp.get(i).getMeanOfPressure(),2);
        }
        return sum / count;
    }

    private double getDiff(String col) {
        List<PressureData> lastSet;
        lastSet = DataSupport.select(col).order("id desc").limit(5).find(PressureData.class);

        double max = lastSet.get(0).getMeanOfPressure();
        double min = lastSet.get(4).getMeanOfPressure();

        return max - min;
    }

//    private double[] getHarr_1 (String col) {
//        double[] result = new double[5];
//        int[] step = {1,3,5,7,9};
//        double sumFirst = 0;
//        double sumLast = 0;
//        for(int i = 0; i < step.length; i++) {
//            List<PressureData> firstSet = DataSupport.select(col).order("id asc").limit(step[i]).find(PressureData.class);
//            for(PressureData data: firstSet){
//                sumFirst += data.getMeanOfPressure();
//            }
//
//            List<PressureData> lastSet = DataSupport.select(col).order("id desc").limit(step[i]).find(PressureData.class);
//            for(PressureData data: lastSet){
//                sumLast += data.getMeanOfPressure();
//            }
//
//            result[i] = sumLast - sumFirst;
//        }
//
//        return result;
//    }
//
//    private double[] getHarr_2 (String col) {
//        int count = DataSupport.count(PressureData.class);
//        double[] result = new double[6];
//        int[] step = {2,4,6,8,10,12};
//        double sumFirst = 0;
//        double sumLast = 0;
//        for(int i = 0; i < step.length; i++) {
//
//            int num = count / step[i];
//
//            List<PressureData> firstSet = DataSupport.select(col).order("id asc").limit(num).find(PressureData.class);
//            for(PressureData data: firstSet){
//                sumFirst += data.getMeanOfPressure();
//            }
//
//            List<PressureData> lastSet = DataSupport.select(col).order("id desc").limit(num).find(PressureData.class);
//            for(PressureData data: lastSet){
//                sumLast += data.getMeanOfPressure();
//            }
//
//            result[i] = sumLast - sumFirst;
//        }
//
//        return result;
//    }
//
//
//    private double getMax(String col) {
//        return DataSupport.max(PressureData.class, col, double.class);
//    }
//
//    private double getMin(String col) {
//        return DataSupport.min(PressureData.class, col, double.class);
//    }
}
