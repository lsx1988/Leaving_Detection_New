package com.shixun.android.leaving_detection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by shixunliu on 15/4/17.
 */

public class GeneralFragment extends Fragment {

    protected int getLayoutID(){
        return 0;
    }

    private static GeneralFragment fragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("login", "onCreate");
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutID(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).onMenuClick();
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
