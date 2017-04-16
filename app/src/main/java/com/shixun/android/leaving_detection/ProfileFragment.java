package com.shixun.android.leaving_detection;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;

import butterknife.BindView;

/**
 * Created by shixunliu on 16/4/17.
 */

public class ProfileFragment extends GeneralFragment {

    @BindView(R.id.profile_username)
    TextView mUsername;
    @BindView(R.id.profile_wifi)
    TextView mWifi;
    @BindView(R.id.profile_magnetic)
    TextView mMagnetic;
    @BindView(R.id.profile_accel)
    TextView mAccel;
    @BindView(R.id.profile_private_model)
    TextView mPrivateModel;
    @BindView(R.id.profile_public_model)
    TextView mPublicModel;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onResume() {
        updateUI();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.profile));
        super.onResume();
    }

    private void updateUI() {

        mUsername.setText(AVUser.getCurrentUser().getUsername());

        if (AVUser.getCurrentUser().has("accel")) {
            mAccel.setText(String.valueOf(AVUser.getCurrentUser().getBoolean("accel")));
        } else {
            mAccel.setText("Not Valid");
        }

        if (AVUser.getCurrentUser().has("magnetic")) {
            mMagnetic.setText(String.valueOf(AVUser.getCurrentUser().getBoolean("magnetic")));
        } else {
            mMagnetic.setText("Not Valid");
        }

        if (AVUser.getCurrentUser().has("wifi")) {
            mWifi.setText(String.valueOf(AVUser.getCurrentUser().getBoolean("wifi")));
        } else {
            mWifi.setText("Not Valid");
        }
    }
}
