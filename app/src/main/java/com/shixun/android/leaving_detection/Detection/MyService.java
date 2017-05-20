package com.shixun.android.leaving_detection.Detection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.shixun.android.leaving_detection.DataCollection.MagneticProcessRunnable;
import com.shixun.android.leaving_detection.DataCollection.Message;
import com.shixun.android.leaving_detection.DataCollection.PressureProcessRunnable;
import com.shixun.android.leaving_detection.DataCollection.TemperatureProcessRunnable;
import com.shixun.android.leaving_detection.DataCollection.WifiScanRunnable;
import com.shixun.android.leaving_detection.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyService extends Service{

    private static final String TAG      = "MyService";
    private WifiManager wifiManager      = null;
    private SensorManager mSensorManager = null;
    private HandlerThread sensorHandlerThread;
    private Handler sensorThreadHandler;
    private ExecutorService fixedThreadPool;
    private List<Double> pressureDataList;
    private List<Double> magneticDataList;
    private List<Double> temperatureList;
    private List<Float> stepList;
    private String[] strArray = new String[4];
    private double[] doubleArray = new double[4];
    private boolean isPressureOn;
    private boolean isMagneticOn;
    private boolean isWifiscanOn;
    private boolean isTemperatureOn;
    private long lastTimeStamp;
    private int lastSize;
    private int count = 0;

    private String str = "";
    private float stepVar;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isPressureOn = (boolean) intent.getExtras().get(getResources().getString(R.string.key_pressure_on));
        isMagneticOn = (boolean) intent.getExtras().get(getResources().getString(R.string.key_magnetic_on));
        isWifiscanOn = (boolean) intent.getExtras().get(getResources().getString(R.string.key_wifi_scan_on));
        isTemperatureOn = (boolean) intent.getExtras().get(getResources().getString(R.string.key_temperature_on));

        lastTimeStamp    = 0;
        pressureDataList = new ArrayList<>();
        magneticDataList = new ArrayList<>();
        temperatureList  = new ArrayList<>();
        stepList = new ArrayList<>();

        wifiManager    = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorHandlerThread = new HandlerThread("sensorListenThread");
        sensorHandlerThread.start();
        sensorThreadHandler = new Handler(sensorHandlerThread.getLooper());
        registerSensor(sensorThreadHandler);

        fixedThreadPool = Executors.newCachedThreadPool();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        sensorHandlerThread.quit();
        fixedThreadPool.shutdown();
        mSensorManager.unregisterListener(mSensorEventListener);
    }


    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentTimeStamp = event.timestamp / 1000000;
            switch(event.sensor.getType()) {

                case Sensor.TYPE_PRESSURE:
                    pressureDataList.add(Double.parseDouble(Float.toString(event.values[0])));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    double x = event.values[0];
                    double y = event.values[1];
                    double z = event.values[2];
                    double magnetic = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
                    magneticDataList.add(magnetic);
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    temperatureList.add(Double.parseDouble(Float.toString(event.values[0])));
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    Log.d(TAG, "计步器触发: " + event.values[0]);
                    stepList.add(event.values[0]);
            }

            if (currentTimeStamp - lastTimeStamp >= 1000) {
                if(isWifiscanOn) {
                    fixedThreadPool.execute(new WifiScanRunnable(wifiManager, strArray, doubleArray));
                }

                if(isPressureOn) {
                    List<Double> temp = new ArrayList<>(pressureDataList);
                    pressureDataList.clear();
                    fixedThreadPool.execute(new PressureProcessRunnable(temp, strArray, doubleArray));
                }

                if(isMagneticOn) {
                    List<Double> temp = new ArrayList<>(magneticDataList);
                    magneticDataList.clear();
                    fixedThreadPool.execute(new MagneticProcessRunnable(temp, strArray, doubleArray));
                }

                if(isTemperatureOn) {
                    List<Double> temp = new ArrayList<>(temperatureList);
                    temperatureList.clear();
                    fixedThreadPool.execute(new TemperatureProcessRunnable(temp, strArray, doubleArray));
                }

                if(stepList.size() > 10) {
                    int currentIndex = stepList.size() - 1;
                    int lastIndex = currentIndex - 5;
                    stepVar = stepList.get(currentIndex) - stepList.get(lastIndex);
                }

                if((isPressureOn == (strArray[0]!=null))
                        && (isMagneticOn == (strArray[2]!=null))
                        && (isWifiscanOn == (strArray[1]!=null))
                        && (isTemperatureOn == (strArray[3]!=null))) {
                    for(String s: strArray) {
                        if(s != null) {
                            str = str + s;
                        }
                    }

//                    if(stepList.size() == lastSize) {
//                        stepVar = 0;
//                        if(lastSize != 0) {
//                            stepList.remove(0);
//                            lastSize--;
//
//                        }
//                    } else {
//                        lastSize = stepList.size();
//                    }
                    EventBus.getDefault().post(new Message(str,
                            doubleArray[0],
                            doubleArray[2],
                            doubleArray[3],
                            doubleArray[1]));
                    str = "";
                }

                lastTimeStamp = currentTimeStamp;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void registerSensor(Handler handler) {
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mSensorManager.registerListener(mSensorEventListener, sensor, 100000, handler);
            }
            if (sensor.getType() == Sensor.TYPE_PRESSURE && isPressureOn == true) {
                mSensorManager.registerListener(mSensorEventListener, sensor, 100000, handler);
            }

            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && isMagneticOn == true) {
                mSensorManager.registerListener(mSensorEventListener, sensor, 100000, handler);
            }

            if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE && isTemperatureOn == true) {
                mSensorManager.registerListener(mSensorEventListener, sensor, 100000, handler);
            }

            if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                mSensorManager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL, handler);
            }
        }
    }
}
