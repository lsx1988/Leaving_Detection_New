package com.shixun.android.leaving_detection.Detection;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by shixunliu on 22/4/17.
 */

public class WifiScanRunnable implements Runnable {

    private static final String TAG = "WifiScanRunnable";
    private WifiManager mWifiManager = null;
    private int dataSize = 10;
    private String[] strArray;

    public WifiScanRunnable(WifiManager wifiManager, String[] strArray) {
        this.mWifiManager = wifiManager;
        this.strArray = strArray;
    }

    @Override
    public void run() {
        scanAndSaveWifiData();
        if (DataSupport.count(WifiData.class) >= dataSize) {
            strArray[1] = (" 18:" + getMean("homeWifiLevel", WifiData.class)
                    + " 19:" + getMean("meanOfAllWifiLevel", WifiData.class))
                    + " 20:" + getStd("stdOfAllWifiLevel")
                    + " 21:" + getSumVar("homeWifiLevel")
                    + " 22:" + getSumVar("meanOfAllWifiLevel");
            //删除一组数据
            int id = DataSupport.findFirst(WifiData.class).getId();
            DataSupport.delete(WifiData.class, id);
        }
    }

    private void scanAndSaveWifiData() {

        WifiInfo wifiInfo = null;
        double isHomeWifi = 0;
        double allWifiLevel = 0;
        double meanOfAllWifi = 0;
        double homeWifiLevel = 0;

        mWifiManager.startScan();
        wifiInfo = mWifiManager.getConnectionInfo();

        homeWifiLevel = wifiInfo.getRssi();
        if (Math.abs(homeWifiLevel) >= 95) {
            homeWifiLevel = -95;
            isHomeWifi = 0.0;
        } else {
            isHomeWifi = 1.0;
        }

        List<ScanResult> scanResults = mWifiManager.getScanResults();
        for (ScanResult scanResult : scanResults) {
            allWifiLevel = allWifiLevel + scanResult.level;
        }
        meanOfAllWifi = allWifiLevel / scanResults.size();

        WifiData sample = new WifiData();
        sample.setHomeWifiLevel(homeWifiLevel);
        sample.setMeanOfAllWifiLevel(meanOfAllWifi);
        sample.setIsHomeWifi(isHomeWifi);
        sample.setStdOfAllWifiLevel(meanOfAllWifi);
        sample.saveThrows();
    }

    private double getSumVar(String col) {

        double average_first = 0, average_last = 0;
        List<WifiData> firstSet, lastSet;
        firstSet = DataSupport.select(col).order("id asc").limit(5).find(WifiData.class);
        lastSet = DataSupport.select(col).order("id desc").limit(5).find(WifiData.class);
        switch(col) {
            case "homeWifiLevel":
                for (WifiData data:firstSet) {
                    average_first += data.getHomeWifiLevel();
                }
                for (WifiData data:lastSet) {
                    average_last += data.getHomeWifiLevel();
                }

                break;
            case "meanOfAllWifiLevel":
                for (WifiData data:firstSet) {
                    average_first += data.getMeanOfAllWifiLevel();
                }

                for (WifiData data:lastSet) {
                    average_last += data.getMeanOfAllWifiLevel();
                }
                break;
        }
        average_first = average_first / firstSet.size();
        average_last = average_last / lastSet.size();
        return Math.abs(average_last-average_first);
    }

    private double getMean(String col, Class className) {
        double mean = DataSupport.average(className, col);
        return mean;
    }

    private double getStd(String col) {
        List<WifiData> temp = DataSupport.select(col).find(WifiData.class);
        double mean = DataSupport.average(WifiData.class, col);
        double result = 0;
        for (int i = 0; i < temp.size() - 1; i++) {
            result += Math.pow(temp.get(i).getStdOfAllWifiLevel() - mean, 2);
        }
        return Math.sqrt(result / (temp.size() - 2));
    }
}
