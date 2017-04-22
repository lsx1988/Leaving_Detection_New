package com.shixun.android.leaving_detection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 15/4/17.
 */

public class SettingFragment extends GeneralFragment implements
        com.kyleduo.switchbutton.SwitchButton.OnCheckedChangeListener {

    @BindView(R.id.switch_button_pressure)
    com.kyleduo.switchbutton.SwitchButton mPressure;
    @BindView(R.id.switch_button_megnatic)
    com.kyleduo.switchbutton.SwitchButton mMegnatic;
    @BindView(R.id.switch_button_wifi)
    com.kyleduo.switchbutton.SwitchButton mWifi;
    @BindView(R.id.save_setting)
    Button saveSetting;
    @BindView(R.id.setting_progress)
    ProgressBar mProgressView;
    @BindView(R.id.setting_form)
    LinearLayout mSettingFormView;
    @BindView(R.id.rb_custom_model)
    RadioButton mCustomModel;
    @BindView(R.id.rb_default_model)
    RadioButton mDefaultModel;
    @BindView(R.id.setting_remodel)
    CheckBox mRemodel;

    private boolean originalPressure;
    private boolean originalMegnatic;
    private boolean originalWifi;

    SensorManager mSensorManager;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        updateSwitchFromLeanCloud();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.setting));
        setHasOptionsMenu(true);
        super.onResume();
    }

    @OnClick(R.id.save_setting)
    public void saveSetting() {
        showProgress(true);

        if(mRemodel.isChecked()){
            if(getActivity() instanceof ActionListener) {
                ((ActionListener) getActivity()).onRemodel();
                saveUserInfo();
            }
        } else if(!mRemodel.isChecked()) {
            if(getActivity() instanceof ActionListener) {
                ((ActionListener) getActivity()).onDetection();
                saveUserInfo();
            }
        }
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
            case R.id.switch_button_wifi:
                break;
        }

        if(mSensor == null) {
            return false;
        }
        return true;
    }

    private void updateSwitchFromLeanCloud() {
        if (AVUser.getCurrentUser().has("pressure")) {
            mPressure.setChecked(AVUser.getCurrentUser().getBoolean("pressure"));
        } else {
            mPressure.setChecked(checkSensor(R.id.switch_button_pressure));
        }

        if (AVUser.getCurrentUser().has("magnetic")) {
            mMegnatic.setChecked(AVUser.getCurrentUser().getBoolean("magnetic"));
        } else {
            mMegnatic.setChecked(checkSensor(R.id.switch_button_megnatic));
        }

        if (AVUser.getCurrentUser().has("wifi")) {
            mWifi.setChecked(AVUser.getCurrentUser().getBoolean("wifi"));
        } else {
            mWifi.setChecked(true);
        }

        if (AVUser.getCurrentUser().has("DefaultModel")) {
            mDefaultModel.setChecked(AVUser.getCurrentUser().getBoolean("DefaultModel"));
        }

        if (AVUser.getCurrentUser().has("CustomModel")) {
            mCustomModel.setChecked(AVUser.getCurrentUser().getBoolean("CustomModel"));
        }

        mPressure.setOnCheckedChangeListener(this);
        mWifi.setOnCheckedChangeListener(this);
        mMegnatic.setOnCheckedChangeListener(this);
        mCustomModel.setOnCheckedChangeListener(this);
        mDefaultModel.setOnCheckedChangeListener(this);
        mRemodel.setOnCheckedChangeListener(this);

        originalMegnatic = mMegnatic.isChecked();
        originalPressure = mPressure.isChecked();
        originalWifi = mWifi.isChecked();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_button_pressure:
                if (!checkSensor(R.id.switch_button_pressure)) {
                    Toast.makeText(getContext(), "The Pressure meter is not available", Toast.LENGTH_SHORT).show();
                    mPressure.setChecked(false);
                }
                if(isChecked && mDefaultModel.isChecked() && !mCustomModel.isChecked() ) {
                    Toast.makeText(getContext(), "Default model only requires Wifi Scan", Toast.LENGTH_SHORT).show();
                    mPressure.setChecked(false);
                }

                if(isChecked != originalPressure && mCustomModel.isChecked()){
                    Toast.makeText(getContext(), "Sensor set is changed, you must re-model", Toast.LENGTH_SHORT).show();
                    mRemodel.setChecked(true);
                } else {
                    mRemodel.setChecked(false);
                }

                break;
            case R.id.switch_button_megnatic:
                if (!checkSensor(R.id.switch_button_megnatic)) {
                    Toast.makeText(getContext(), "The Megnatic meter is not available", Toast.LENGTH_SHORT).show();
                    mMegnatic.setChecked(false);
                }
                if(isChecked && mDefaultModel.isChecked() && !mCustomModel.isChecked()) {
                    Toast.makeText(getContext(), "Default model only requires Wifi Scan", Toast.LENGTH_SHORT).show();;
                    mMegnatic.setChecked(false);
                }

                if(isChecked != originalMegnatic && mCustomModel.isChecked()){
                    Toast.makeText(getContext(), "Sensor set is changed, you must re-model", Toast.LENGTH_SHORT).show();
                    mRemodel.setChecked(true);
                } else {
                    mRemodel.setChecked(false);
                }

                break;
            case R.id.switch_button_wifi:
                if(!isChecked) {
                    Toast.makeText(getContext(), "We highly recommend to connect the private Wifi", Toast.LENGTH_SHORT).show();
                }

                if(isChecked != originalWifi && mCustomModel.isChecked()){
                    Toast.makeText(getContext(), "Sensor set is changed, you must re-model", Toast.LENGTH_SHORT).show();
                    mRemodel.setChecked(true);
                } else {
                    mRemodel.setChecked(false);
                }
                break;

            case R.id.rb_default_model:
                if(isChecked) {
                    mWifi.setChecked(true);
                    mPressure.setChecked(false);
                    mMegnatic.setChecked(false);
                    mRemodel.setChecked(false);
                }
                break;
            case R.id.rb_custom_model:
                if(isChecked) {
                    mPressure.setChecked(originalPressure);
                    mMegnatic.setChecked(originalMegnatic);
                    mWifi.setChecked(originalWifi);
                }
                break;

            case R.id.setting_remodel:
                if(mDefaultModel.isChecked() && !mCustomModel.isChecked()) {
                    Toast.makeText(getContext(), "You CAN NOT modify the default model", Toast.LENGTH_SHORT).show();
                    mRemodel.setChecked(false);
                }
                break;
            default:
                break;
        }
        if(!mPressure.isChecked() && !mMegnatic.isChecked() && !mWifi.isChecked()) {
            Toast.makeText(getContext(), "At least ONE SENSOR should be open", Toast.LENGTH_SHORT).show();
            mWifi.setChecked(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSettingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSettingFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSettingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSettingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void saveUserInfo() {
        AVUser.getCurrentUser().put("pressure", mPressure.isChecked());
        AVUser.getCurrentUser().put("magnetic", mMegnatic.isChecked());
        AVUser.getCurrentUser().put("wifi", mWifi.isChecked());
        AVUser.getCurrentUser().put("DefaultModel", mDefaultModel.isChecked());
        AVUser.getCurrentUser().put("CustomModel", mCustomModel.isChecked());
        AVUser.getCurrentUser().saveInBackground();
    }
}
