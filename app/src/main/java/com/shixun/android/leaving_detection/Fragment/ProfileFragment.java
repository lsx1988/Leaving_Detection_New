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
    @BindView(R.id.profile_private_model)
    TextView mCustomModel;
    @BindView(R.id.profile_public_model)
    TextView mDefaultModel;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onResume() {
        updateUI(getArguments());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.profile));
        super.onResume();
    }

    private void updateUI(Bundle bundle) {

        if (bundle == null) {
            mPressure.setText("Not Valid");
            mMagnetic.setText("Not Valid");
            mWifi.setText("Not Valid");
            mTemperature.setText("Not Valid");
            mDefaultModel.setText("Not Valid");
            mCustomModel.setText("Not Valid");
        } else {
            mPressure.setText(String.valueOf(bundle.getBoolean("pressure")));
            mMagnetic.setText(String.valueOf(bundle.getBoolean("magnetic")));
            mWifi.setText(String.valueOf(bundle.getBoolean("wifi")));
            mTemperature.setText(String.valueOf(bundle.getBoolean("temperature")));
            mDefaultModel.setText(String.valueOf(bundle.getBoolean("default model")));
            mCustomModel.setText(String.valueOf(bundle.getBoolean("custom model")));
        }
    }
}
