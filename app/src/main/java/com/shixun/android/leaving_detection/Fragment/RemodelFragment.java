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

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.current_location)
    TextView mLocation;

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
        isPressureOn = args.getBoolean("pressure");
        isMagneticOn = args.getBoolean("magnetic");
        isWifiScanOn = args.getBoolean("wifi");
        isTemperatureOn = args.getBoolean("temperature");
        ambientMap = (HashMap<String, String>) args.getSerializable("ambient");

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
        rawData = rawData + String.valueOf(label) + str + "\n";
        Log.d(TAG, str);
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

        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();

            // 在 SD 卡的根目录下创建文件夹
            File folder =  new File(sdCardPath + File.separator + "Sensor_Data");

            if(!folder.exists()) {
                folder.mkdir();
            }

        /*
        * 文件输出操作
        * */
            File testFile = new File(folder, fileName);
            // 初始化文件输出流
            FileOutputStream fileOutputStream = null;
            // 以追加模式打开文件输出流
            try {
                fileOutputStream = new FileOutputStream(testFile, true);
                fileOutputStream.write(content.getBytes());
                // 关闭文件输出流
                fileOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "内部文件");
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
