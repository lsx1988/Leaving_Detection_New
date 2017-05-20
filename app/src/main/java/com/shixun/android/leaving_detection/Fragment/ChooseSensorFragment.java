package com.shixun.android.leaving_detection.Fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_add_model;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if(savedInstanceState != null) {
            mPressure.setChecked(savedInstanceState.getBoolean(getString(R.string.key_pressure_on)));
            mMegnatic.setChecked(savedInstanceState.getBoolean(getString(R.string.key_magnetic_on)));
            mTemperature.setChecked(savedInstanceState.getBoolean(getString(R.string.key_temperature_on)));
            mWifi.setChecked(savedInstanceState.getBoolean(getString(R.string.key_wifi_scan_on)));
        }

        mPressure.setOnCheckedChangeListener(this);
        mMegnatic.setOnCheckedChangeListener(this);
        mTemperature.setOnCheckedChangeListener(this);
        mWifi.setOnCheckedChangeListener(this);
    }

    @Override
    public void onResume() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.select_sensor_title));
        setHasOptionsMenu(true);
        super.onResume();
    }

    @OnClick(R.id.collect_data)
    public void saveSetting() {
        if(!mPressure.isChecked() && !mMegnatic.isChecked() && !mWifi.isChecked() && !mTemperature.isChecked()) {
            showSnackBar(getString(R.string.error_one_sensor_at_least));
        } else {
            if(getActivity() instanceof ActionListener) {
                ((ActionListener) getActivity()).onSaveSensorChosen(bindData());
                ((ActionListener) getActivity()).onRemodel();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!checkSensor(buttonView.getId())) {
            showSnackBar(getString(R.string.error_sensor_not_available));
            buttonView.setChecked(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).onSaveSensorChosen(bindData());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.key_pressure_on), mPressure.isChecked());
        outState.putBoolean(getString(R.string.key_magnetic_on), mMegnatic.isChecked());
        outState.putBoolean(getString(R.string.key_wifi_scan_on), mWifi.isChecked());
        outState.putBoolean(getString(R.string.key_temperature_on), mTemperature.isChecked());
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
                break;
            case R.id.switch_button_wifi:
                return true;
        }
        if(mSensor == null) {
            return false;
        }
        return true;
    }

    private Bundle bindData() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(getString(R.string.key_pressure_on), mPressure.isChecked());
        bundle.putBoolean(getString(R.string.key_magnetic_on), mMegnatic.isChecked());
        bundle.putBoolean(getString(R.string.key_wifi_scan_on), mWifi.isChecked());
        bundle.putBoolean(getString(R.string.key_temperature_on), mTemperature.isChecked());
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
