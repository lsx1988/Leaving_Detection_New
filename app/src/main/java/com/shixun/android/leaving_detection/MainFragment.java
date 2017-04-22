package com.shixun.android.leaving_detection;

import android.app.Service;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shixun.android.leaving_detection.Detection.MessageEvent;
import com.shixun.android.leaving_detection.Detection.MessageStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 16/4/17.
 */

public class MainFragment extends GeneralFragment {

    private Vibrator vibrator;
    private Intent serviceIntent;
    private double wifiLevel;
    private double possibility;
    private double predict;
    private double pressure;
    private boolean  stayInside= false, stayOutside= false;

    @BindView(R.id.wifi_level)
    TextView mWifiLevelTextView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.possibility)
    TextView mPossibilityTextView;
    @BindView(R.id.pressure_level)
    TextView mPressureTextView;
    @BindView(R.id.use_pressure)
    CheckBox isPressureOnCheckBox;
    @BindView(R.id.status)
    TextView mStatusTextView;
    @BindView(R.id.walking)
    TextView isWalking;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main;
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        LitePal.initialize(getActivity());
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        super.onResume();
    }

    /**
     *  press the start button, the app will show the progress bar to indicate the data collecting
     *  phase. Create the intent and start the service. If the service is already in operation, the
     *  activity will not activate the service again.
     */

    @OnClick(R.id.start_detection) void start() {
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).startDetection(false);
        }
        showProgressBar();
        mStatusTextView.setText("Initializing");
    }

    /**
     * when press the stop button, unregister the eventbus, stop the service and showup the pressure
     * selection. And only when stop button is clicked, the service will be stopped.
     */

    @OnClick(R.id.stop_detection) void stop() {
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).stopDetection();
        }

        mStatusTextView.setText("");
        mStatusTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.notWalking));
    }

    /**
     * when the activity is not visible to user, unregister the eventbus.
     */

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * when receive the message, update the UI view.
     * @param event is the messageEvent send from background thread
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        updateUI(event, mWifiLevelTextView, mPossibilityTextView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageStatus(MessageStatus event) {
        boolean status = event.isWalking();
        if (status == true) {
            mStatusTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.isWalking));
            mStatusTextView.setText("You are walking");
        } else {
            mStatusTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.notWalking));
            mStatusTextView.setText("Not walking");
        }
    }

    private void updateUI(MessageEvent event, TextView mWifiLevelTextView, TextView mPossibilityTextView) {

        if(isPressureOnCheckBox.isChecked() == true) {
            showWifiAndPressureData();
        } else {
            showWifiData();
        }

        wifiLevel = event.getWifiLevel();
        possibility = event.getPossibility();
        predict = event.getPredict();
        pressure = event.getPressure();

        mWifiLevelTextView.setText(String.valueOf("Wifi :" + wifiLevel));
        mPossibilityTextView.setText(String.format("Possibility :" + "%.2f", possibility * 100) + "%");
        mPressureTextView.setText(String.format("Pressure :" + "%.2f", pressure) + "Pa");

        if (predict == 1.0) {

            if (possibility >= 0.95) {
                stayOutside = true;
            }

            if (stayOutside == true && stayInside == true) {
                vibrator.vibrate(2000);
                stayInside = false;
            }

        }else if (predict != 1.0) {

            if (possibility <= 0.1) {
                stayInside = true;
            }

            if (stayInside == true && stayOutside == true) {
                vibrator.vibrate(2000);
                stayOutside = false;
            }
        }
    }

    private void showProgressBar() {
        isPressureOnCheckBox.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void showWifiData() {
        mProgressBar.setVisibility(View.GONE);
        mWifiLevelTextView.setVisibility(View.VISIBLE);
        mPossibilityTextView.setVisibility(View.VISIBLE);
    }

    private void showWifiAndPressureData() {
        mProgressBar.setVisibility(View.GONE);
        mWifiLevelTextView.setVisibility(View.VISIBLE);
        mPressureTextView.setVisibility(View.VISIBLE);
        mPossibilityTextView.setVisibility(View.VISIBLE);
    }
}

