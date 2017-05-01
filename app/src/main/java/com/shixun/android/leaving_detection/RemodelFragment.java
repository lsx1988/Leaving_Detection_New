package com.shixun.android.leaving_detection;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shixun.android.leaving_detection.Detection.MagneticData;
import com.shixun.android.leaving_detection.Detection.Message;
import com.shixun.android.leaving_detection.Detection.PressureData;
import com.shixun.android.leaving_detection.Detection.WifiData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 22/4/17.
 */

public class RemodelFragment extends GeneralFragment {

    private static final String TAG = "RemodelFragment";

    @BindView(R.id.remodel_start)
    Button mStart;
    @BindView(R.id.remodel_leaving)
    Button mLeaving;
    @BindView(R.id.remodel_stop)
    Button mStop;

    String label = "0";
    String rawData = "";

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
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.remodel_start)
    public void start() {
        deleteFile("sensorCollection.txt");
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).startDetection(true);
        }
        mStart.setVisibility(View.GONE);
        mLeaving.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.remodel_leaving)
    public void leaving() {
        label = "1";
        mLeaving.setVisibility(View.GONE);
        mStop.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.remodel_stop)
    public void stop() {

        writeFiles(rawData, "sensorCollection.txt");
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).stopDetection();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).stopDetection();
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message event) {
        String str = event.getMessage();
        rawData = rawData + label + str + "\n";
        Log.d(TAG, str);
    }

    public void writeFiles (String content, String fileName) {

        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD卡");

            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();
            Log.d(TAG, sdCardPath.toString());
        /*
        * 文件输出操作
        * */
            File testFile = new File(sdCardPath, fileName);
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

    public void deleteFile(String fileName) {
        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();
        /*
        * 文件输出操作
        * */
            File testFile = new File(sdCardPath, fileName);

            if(testFile.exists()) {
                testFile.delete();
            }
        } else {
            File file = new File(fileName);
            if(file.exists()) {
                file.delete();
            }
        }
    }
}
