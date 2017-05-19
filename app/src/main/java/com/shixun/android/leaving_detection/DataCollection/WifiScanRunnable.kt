package com.shixun.android.leaving_detection.DataCollection

import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import org.litepal.crud.DataSupport

/**
 * Created by shixunliu on 22/4/17.
 */

class WifiScanRunnable(wifiManager: WifiManager, private val strArray: Array<String>) : Runnable {

    private var mWifiManager: WifiManager
    private val dataSize = 20

    init {
        this.mWifiManager = wifiManager
    }

    override fun run() {
        scanAndSaveWifiData()
        if (DataSupport.count(WifiData::class.java) >= dataSize) {
            val currentValue = getMean("homeWifiLevel", WifiData::class.java)
            val std = getStd("meanOfHomeWifi")
            val sumVar = getSumVar("meanOfHomeWifi")
            strArray[1] = " 30:$currentValue 31:$std 32:$sumVar"

            //删除一组数据
            val id = DataSupport.findFirst<WifiData>(WifiData::class.java).id
            DataSupport.delete(WifiData::class.java, id.toLong())
        }
    }

    private fun scanAndSaveWifiData() {

        var wifiInfo: WifiInfo? = null
        var isHomeWifi = 0.0
        var allWifiLevel = 0.0
        var meanOfAllWifi = 0.0
        var homeWifiLevel = 0.0

        mWifiManager.startScan()
        wifiInfo = mWifiManager.connectionInfo

        homeWifiLevel = wifiInfo!!.rssi.toDouble()
        if (Math.abs(homeWifiLevel) >= 95) {
            homeWifiLevel = -95.0
            isHomeWifi = 0.0
        } else {
            isHomeWifi = 1.0
        }

        val scanResults = mWifiManager.scanResults
        for (scanResult in scanResults) {
            allWifiLevel = allWifiLevel + scanResult.level
        }
        meanOfAllWifi = allWifiLevel / scanResults.size

        val sample = WifiData()
        sample.homeWifiLevel = homeWifiLevel
        sample.meanOfAllWifiLevel = meanOfAllWifi
        sample.isHomeWifi = isHomeWifi
        sample.stdOfAllWifiLevel = meanOfAllWifi
        if (DataSupport.count(WifiData::class.java) == 0) {
            sample.meanOfHomeWifi = homeWifiLevel
        } else {
            sample.meanOfHomeWifi = getMean("homeWifiLevel", WifiData::class.java)
        }
        sample.saveThrows()
    }

    private fun getSumVar(col: String): Double {
        val lastSet: List<WifiData>
        //lastSet = DataSupport.select(col).order("id desc").limit(10).find(WifiData.class);
        lastSet = DataSupport.select(col).find<WifiData>(WifiData::class.java)
        val size = lastSet.size

        val max = lastSet[size - 1].meanOfHomeWifi
        val min = lastSet[0].meanOfHomeWifi!!

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
