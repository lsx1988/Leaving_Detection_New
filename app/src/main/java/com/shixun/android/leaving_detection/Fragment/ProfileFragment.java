package com.shixun.android.leaving_detection.Fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.shixun.android.leaving_detection.R;

import butterknife.BindView;

/**
 * Created by shixunliu on 16/4/17.
 */

public class ProfileFragment extends GeneralFragment {

    @BindView(R.id.profile_wifi)
    TextView mWifi;
    @BindView(R.id.profile_magnetic)
    TextView mMagnetic;
    @BindView(R.id.profile_pressure)
    TextView mPressure;
    @BindView(R.id.profile_temperature)
    TextView mTemperature;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onResume() {
        updateUI(getArguments());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.menu_profile));
        super.onResume();
    }

    private void updateUI(Bundle bundle) {

        if (bundle == null) {
            mPressure.setText(getString(R.string.not_valid));
            mMagnetic.setText(getString(R.string.not_valid));
            mWifi.setText(getString(R.string.not_valid));
            mTemperature.setText(getString(R.string.not_valid));
        } else {
            mPressure.setText(String.valueOf(bundle.getBoolean(getString(R.string.key_pressure_on))));
            mMagnetic.setText(String.valueOf(bundle.getBoolean(getString(R.string.key_magnetic_on))));
            mWifi.setText(String.valueOf(bundle.getBoolean(getString(R.string.key_wifi_scan_on))));
            mTemperature.setText(String.valueOf(bundle.getBoolean(getString(R.string.key_temperature_on))));
        }
    }
}
