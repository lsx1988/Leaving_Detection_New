package com.shixun.android.leaving_detection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 22/4/17.
 */

public class RemodelFragment extends GeneralFragment {

    @BindView(R.id.remodel_start)
    Button mStart;
    @BindView(R.id.remodel_leaving)
    Button mLeaving;
    @BindView(R.id.remodel_stop)
    Button mStop;

    String label = "0";

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_remodel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        LitePal.initialize(getActivity());
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.remodel_start)
    public void start() {
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).startDetection(true);
        }
    }
}
