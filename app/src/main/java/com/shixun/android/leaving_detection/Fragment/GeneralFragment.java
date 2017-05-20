package com.shixun.android.leaving_detection.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shixun.android.leaving_detection.Activity.ActionListener;

import butterknife.ButterKnife;

/**
 * Created by shixunliu on 15/4/17.
 */

public class GeneralFragment extends Fragment {

    protected int getLayoutID(){
        return 0;
    }
    private static GeneralFragment fragment;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutID(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((ActionListener) getActivity()).onUpNevigationClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static GeneralFragment newInstance() {

        Bundle args = new Bundle();
        if(fragment == null) {
            fragment = new GeneralFragment();
            fragment.setArguments(args);
        }
        return fragment;
    }
}
