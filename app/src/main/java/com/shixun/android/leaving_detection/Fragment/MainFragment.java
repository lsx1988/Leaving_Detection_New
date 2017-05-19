package com.shixun.android.leaving_detection.Fragment;

import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shixun.android.leaving_detection.Activity.ActionListener;
import com.shixun.android.leaving_detection.DataCollection.MagneticData;
import com.shixun.android.leaving_detection.DataCollection.Message;
import com.shixun.android.leaving_detection.DataCollection.PressureData;
import com.shixun.android.leaving_detection.DataCollection.TemperatureData;
import com.shixun.android.leaving_detection.DataCollection.WifiData;
import com.shixun.android.leaving_detection.LibSVM.svm_predict_Detection;
import com.shixun.android.leaving_detection.LibSVM.svm_scale_Detection;
import com.shixun.android.leaving_detection.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 16/4/17.
 */

public class MainFragment extends GeneralFragment {

    private static final String TAG = "MainFragment";
    private Vibrator vibrator;
    private double possibility;
    private double predict;
    private boolean stayInside = false, stayOutside = false;
    private String[] blank = {"-b", "1", "a", "b", "c"};
    private String[] blank_scale = {"-r", "scale_para", "data"};
    private double[] result = new double[2];
    private File modelfile;

    @BindView(R.id.wifi_level)
    TextView mWifiLevelTextView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.possibility)
    TextView mPossibilityTextView;
    @BindView(R.id.pressure_level)
    TextView mPressureTextView;
    @BindView(R.id.walking)
    TextView isWalking;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        Bundle args = getArguments();
        if(args != null) {
            modelfile = (File) args.getSerializable("model");
            boolean loading = args.getBoolean("loading");
            if(loading == true) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("DETECTION");
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        mPossibilityTextView.setVisibility(View.VISIBLE);
        super.onResume();
    }

    /**
     * press the start button, the app will show the progress bar to indicate the data collecting
     * phase. Create the intent and start the service. If the service is already in operation, the
     * activity will not activate the service again.
     */

    @OnClick(R.id.start_detection)
    void start() {
        DataSupport.deleteAll(WifiData.class);
        DataSupport.deleteAll(MagneticData.class);
        DataSupport.deleteAll(PressureData.class);
        DataSupport.deleteAll(TemperatureData.class);
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).showModelTypeDialog();
        }
    }

    /**
     * when press the stop button, unregister the eventbus, stop the service and showup the pressure
     * selection. And only when stop button is clicked, the service will be stopped.
     */

    @OnClick(R.id.stop_detection)
    void stop() {
        if (getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).stopService();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
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
        mProgressBar.setVisibility(View.GONE);
        String str = "0" + event.getMessage();
        Log.d(TAG, str);
        detection(str);
        possibility = result[1];
        predict = result[0];
        Log.d(TAG, String.valueOf(predict));
        mPossibilityTextView.setText(String.format("Possibility :" + "%.2f", possibility * 100) + "%");

        if (predict == 100.0) {
            if (possibility >= 0.95) {
                stayOutside = true;
            }
            if (stayOutside == true && stayInside == true) {
                vibrator.vibrate(1000);
                stayInside = false;
            }
        }else if (predict != 100) {
            if (possibility <= 0.2) {
                stayInside = true;
            }
            if (stayInside == true && stayOutside == true) {
                vibrator.vibrate(1000);
                stayOutside = false;
            }
        }
    }

    private double[] detection(String str) {
        try {
            String scale_result = svm_scale_Detection.main(blank_scale, str, modelfile);
            File file = new File(modelfile.toString() + File.separator + "model.txt");
            BufferedReader model = new BufferedReader(new FileReader(file));
            result = svm_predict_Detection.main(blank, scale_result, model);
            return result;
        } catch (IOException e) {
            return null;
        }
    }
}


