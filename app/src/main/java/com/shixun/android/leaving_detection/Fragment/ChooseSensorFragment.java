package com.shixun.android.leaving_detection.Fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shixun.android.leaving_detection.Activity.ActionListener;
import com.shixun.android.leaving_detection.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 15/4/17.
 */

public class ChooseSensorFragment extends GeneralFragment implements
        com.kyleduo.switchbutton.SwitchButton.OnCheckedChangeListener {

    @BindView(R.id.switch_button_pressure)
    com.kyleduo.switchbutton.SwitchButton mPressure;
    @BindView(R.id.switch_button_megnatic)
    com.kyleduo.switchbutton.SwitchButton mMegnatic;
    @BindView(R.id.switch_button_wifi)
    com.kyleduo.switchbutton.SwitchButton mWifi;
    @BindView(R.id.switch_button_temperature)
    com.kyleduo.switchbutton.SwitchButton mTemperature;
    @BindView(R.id.collect_data)
    Button collectData;

    private SensorManager mSensorManager;

    private final String PRESSURE_KEY = "pressure";
    private final String MAGNETIC_KEY = "magnetic";
    private final String TEMPERATURE_KEY = "temperature";
    private final String WIFI_KEY = "wifi";

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_add_model;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if(savedInstanceState != null) {
            mPressure.setChecked(savedInstanceState.getBoolean(PRESSURE_KEY));
            mMegnatic.setChecked(savedInstanceState.getBoolean(MAGNETIC_KEY));
            mTemperature.setChecked(savedInstanceState.getBoolean(TEMPERATURE_KEY));
            mWifi.setChecked(savedInstanceState.getBoolean(WIFI_KEY));
        }

        mPressure.setOnCheckedChangeListener(this);
        mMegnatic.setOnCheckedChangeListener(this);
        mTemperature.setOnCheckedChangeListener(this);
        mWifi.setOnCheckedChangeListener(this);
    }

    @Override
    public void onResume() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("SELECT SENSOR");
        setHasOptionsMenu(true);
        super.onResume();
    }

    @OnClick(R.id.collect_data)
    public void saveSetting() {
        if(!mPressure.isChecked() && !mMegnatic.isChecked() && !mWifi.isChecked() && !mTemperature.isChecked()) {
            showSnackBar("ONE SENSOR should be selected at least");
        } else {
            if(getActivity() instanceof ActionListener) {
                ((ActionListener) getActivity()).onSendDataBack(bindData());
                ((ActionListener) getActivity()).onRemodel();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_button_pressure:
                if (!checkSensor(R.id.switch_button_pressure)) {
                    showSnackBar("The Pressure meter is not available");
                    mPressure.setChecked(false);
                }
                break;
            case R.id.switch_button_megnatic:
                if (!checkSensor(R.id.switch_button_megnatic)) {
                    showSnackBar("The Magnetic meter is not available");
                    mMegnatic.setChecked(false);
                }
                break;
            case R.id.switch_button_temperature:
                if (!checkSensor(R.id.switch_button_temperature)) {
                    showSnackBar("The Temperature meter is not available");
                    mTemperature.setChecked(false);
                }
            case R.id.switch_button_wifi:
                if(!isChecked) {
                    showSnackBar("Wifi Scan is highly recommended activted");
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).onSendDataBack(bindData());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PRESSURE_KEY, mPressure.isChecked());
        outState.putBoolean(MAGNETIC_KEY, mMegnatic.isChecked());
        outState.putBoolean(WIFI_KEY, mWifi.isChecked());
        outState.putBoolean(TEMPERATURE_KEY, mTemperature.isChecked());
        Log.d("setting", "onSaveInstanceState: ");
    }

    private boolean checkSensor(int id) {
        Sensor mSensor = null;
        switch(id) {
            case R.id.switch_button_pressure:
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                break;
            case R.id.switch_button_megnatic:
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                break;
            case R.id.switch_button_temperature:
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            case R.id.switch_button_wifi:
                break;
        }
        if(mSensor == null) {
            return false;
        }
        return true;
    }

    private Bundle bindData() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PRESSURE_KEY, mPressure.isChecked());
        bundle.putBoolean(MAGNETIC_KEY, mMegnatic.isChecked());
        bundle.putBoolean(WIFI_KEY, mWifi.isChecked());
        bundle.putBoolean(TEMPERATURE_KEY, mTemperature.isChecked());

        return bundle;
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(this.getView(), msg, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_alert, 0, 0, 0);
        snackbar.setDuration(3000).show();
    }
}
