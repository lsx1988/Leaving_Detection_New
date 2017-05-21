package com.shixun.android.leaving_detection.DataCollection

import android.net.wifi.WifiManager
import org.litepal.crud.DataSupport

/**
 * Created by shixunliu on 22/4/17.
 */

class WifiScanRunnable(val wifiManager: WifiManager, private val strArray: Array<String>, private val doubleArray: DoubleArray) : Runnable {

    private val dataSize = 20

    override fun run() {
        scanAndSaveWifiData()
        //val currentValue = scanAndSaveWifiData()
        if (DataSupport.count(WifiData::class.java) >= dataSize) {
            val currentValue = "%.3f".format(getMean("homeWifiLevel", WifiData::class.java))
            val std = "%.3f".format(getStd("meanOfHomeWifi"))
            val sumVar = "%.3f".format(getSumVar("meanOfHomeWifi"))
            doubleArray[1] = currentValue.toDouble()
            strArray[1] = " 30:$currentValue 31:$std 32:$sumVar"

            val lastId = DataSupport.findFirst<WifiData>(WifiData::class.java).id
            DataSupport.delete(WifiData::class.java, lastId.toLong())
        }
    }

    private fun scanAndSaveWifiData(): Double {

//        var allWifiLevel = 0.0
        wifiManager.startScan()
        val wifiInfo = wifiManager.connectionInfo
        var homeWifiLevel = wifiInfo!!.rssi.toDouble()
//        val scanResults = wifiManager.scanResults

//        scanResults.forEach {
//            allWifiLevel += it.level
//        }
//        val meanOfAllWifi = allWifiLevel / scanResults.size

        val sample = WifiData()
        sample.homeWifiLevel = homeWifiLevel
//        sample.meanOfAllWifiLevel = meanOfAllWifi
//        sample.stdOfAllWifiLevel = meanOfAllWifi
        if (DataSupport.count(WifiData::class.java) == 0) {
            sample.meanOfHomeWifi = homeWifiLevel
        } else {
            sample.meanOfHomeWifi = getMean("homeWifiLevel", WifiData::class.java)
        }
        sample.saveThrows()
        return homeWifiLevel
    }

    private fun getSumVar(col: String): Double {
        val lastSet: List<WifiData>
        //lastSet = DataSupport.select(col).order("id desc").limit(10).find(WifiData.class);
        lastSet = DataSupport.select(col).find<WifiData>(WifiData::class.java)
        val size = lastSet.size

        val max = lastSet[size - 1].meanOfHomeWifi
        val min = lastSet[0].meanOfHomeWifi

        return max - min
    }

    private fun getMean(col: String, className: Class<*>): Double {
        val mean = DataSupport.average(className, col)
        return mean
    }

    private fun getStd(col: String): Double {
        val temp = DataSupport.select(col).find<WifiData>(WifiData::class.java)
        val mean = DataSupport.average(WifiData::class.java!!, col)
        var result = 0.0
        for (i in 0..temp.size - 1 - 1) {
            result += Math.pow(temp[i].meanOfHomeWifi!! - mean, 2.0)
        }
        return Math.sqrt(result / (temp.size - 2))
    }
}
