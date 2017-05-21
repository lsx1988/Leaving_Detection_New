package com.shixun.android.leaving_detection.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shixun.android.leaving_detection.Activity.ActionListener;
import com.shixun.android.leaving_detection.DataCollection.MagneticData;
import com.shixun.android.leaving_detection.DataCollection.Message;
import com.shixun.android.leaving_detection.DataCollection.PressureData;
import com.shixun.android.leaving_detection.DataCollection.TemperatureData;
import com.shixun.android.leaving_detection.DataCollection.WifiData;
import com.shixun.android.leaving_detection.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 22/4/17.
 */

public class RemodelFragment extends GeneralFragment {

    private static final String TAG = "RemodelFragment";

    private int label = 0;
    private String rawData = "";
    private boolean isPressureOn, isMagneticOn, isWifiScanOn, isTemperatureOn;
    private HashMap<String, String> ambientMap;
    private String pStr, mStr, wStr, tStr;
    private int btnCount = 0;
    private int count = 10;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.current_location)
    TextView mLocation;
    @BindView(R.id.pressure_level)
    TextView mPressure;
    @BindView(R.id.magnetic_level)
    TextView mMagnetic;
    @BindView(R.id.wifi_level)
    TextView mWifi;
    @BindView(R.id.temperature_level)
    TextView mTemperature;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_remodel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        DataSupport.deleteAll(WifiData.class);
        DataSupport.deleteAll(MagneticData.class);
        DataSupport.deleteAll(PressureData.class);
        DataSupport.deleteAll(TemperatureData.class);

        Bundle args = getArguments();
        isPressureOn = args.getBoolean(getString(R.string.key_pressure_on));
        isMagneticOn = args.getBoolean(getString(R.string.key_magnetic_on));
        isWifiScanOn = args.getBoolean(getString(R.string.key_wifi_scan_on));
        isTemperatureOn = args.getBoolean(getString(R.string.key_temperature_on));
        ambientMap = (HashMap<String, String>) args.getSerializable(getString(R.string.key_ambient));

        if(ambientMap != null) {
            addButtonPerAmbient(ambientMap);
        }

        pStr = String.valueOf((isPressureOn) ? 1 : 0);
        mStr = String.valueOf((isMagneticOn) ? 1 : 0);
        wStr = String.valueOf((isWifiScanOn) ? 1 : 0);
        tStr = String.valueOf((isTemperatureOn) ? 1 : 0);

        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.remodel_start)
    public void start() {
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).startService();
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mLocation.setVisibility(View.VISIBLE);
}

    @OnClick(R.id.remodel_leaving)
    public void leaving() {
        label = 100;
        mLocation.setText("Now Leaving");
    }

    @OnClick(R.id.remodel_stop)
    public void stop() {

        Calendar c = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(c.getTime());
        writeFiles(rawData, currentDate + " _P" + pStr + "_M" + mStr + "_W" + wStr + "_T" + tStr + " .txt");

        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).stopService();
            ((ActionListener) getActivity()).onCollectSensorDataSuccessful();
        }

        DataSupport.deleteAll(WifiData.class);
        DataSupport.deleteAll(MagneticData.class);
        DataSupport.deleteAll(PressureData.class);
        DataSupport.deleteAll(TemperatureData.class);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).stopService();
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message event) {
        String str = event.getMessage();
        mPressure.setText(String.format("Pressure " + "%.2f" + " Pa", event.getPressure()));
        mMagnetic.setText(String.format("Magnetic " + "%.2f" + " uT", event.getMagnetic()));
        mTemperature.setText(String.format("Temperature " + "%.2f" + " \u2103", event.getTemperature()));
        mWifi.setText(String.format("Wifi " + "%.2f" + " dB", event.getWifi()));
        //if(count == 10) {
            rawData = rawData + String.valueOf(label) + str + "\n";
            Log.d(TAG, str);
            count = -1;
        //}
        //count++;

    }

    private void addButtonPerAmbient(HashMap<String, String> ambientMap) {
        final LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.remodel_layout);
        for (final String ambient : ambientMap.keySet()) {
            String number = ambientMap.get(ambient);
            for(int i = 0; i < Integer.parseInt(number); i++) {
                btnCount++;
                Button btn = new Button(getContext());
                btn.setText(ambient + " " + (i+1));
                btn.setId(btnCount);
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.addView(btn);

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        label = view.getId();
                        mLocation.setText("Stay in " + ambient);
                    }
                });
            }
        }
    }

    private void writeFiles (String content, String fileName) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardPath = Environment.getExternalStorageDirectory();
            File folder =  new File(sdCardPath + File.separator + getString(R.string.raw_data_folder));
            if(!folder.exists()) {
                folder.mkdir();
            }

            File testFile = new File(folder, fileName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(testFile, true);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // 初始化文件输出流
            FileOutputStream fileOutputStream = null;
            try {
                // 以追加模式打开文件输出流
                fileOutputStream = getActivity().openFileOutput(fileName,Context.MODE_APPEND);
                fileOutputStream.write(content.getBytes());
                // 关闭文件输出流
                fileOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
