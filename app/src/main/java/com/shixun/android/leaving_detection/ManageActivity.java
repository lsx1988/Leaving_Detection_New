package com.shixun.android.leaving_detection;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ManageActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_fragment);
    }

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }
}
