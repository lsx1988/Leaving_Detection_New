package com.shixun.android.leaving_detection.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shixun.android.leaving_detection.Activity.ActionListener;
import com.shixun.android.leaving_detection.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shixunliu on 1/5/17.
 */

public class AmbientListFragment extends GeneralFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView = null;
    private AmbientAdapter mAmbientAdapter;
    private FloatingActionButton mFloatingActionButton;

    @Override
    public void onResume() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Ambient");
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_file_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAmbientAdapter = new AmbientAdapter();
        mRecyclerView.setAdapter(mAmbientAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.btnFloatingAction);
        mFloatingActionButton.setOnClickListener(this);

        defaultAmbientList();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFloatingAction:
                AddNewAmbientDialog dialog = new AddNewAmbientDialog();
                dialog.setTargetFragment(this, 0);
                dialog.show(getChildFragmentManager(), "AddAmbientDialog");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveAmbientToActivity();
    }

    public void addNewAmbient(String ambient) {
        mAmbientAdapter.addAmbientItem(ambient);
    }

    private void defaultAmbientList() {
        List<String> ambientList = new ArrayList<>();
        ambientList.add("Living Room");
        ambientList.add("Bedroom");
        ambientList.add("Kitchen");
        ambientList.add("Washroom");
        mAmbientAdapter.setAmbientList(ambientList);
    }

    private void saveAmbientToActivity() {
        HashMap<String, String> ambientMap = new HashMap<>();
        int itemNum = mAmbientAdapter.getItemCount();
        for(int i = 0; i < itemNum; i++) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(i);
            String ambient = ((AmbientAdapter.AmbientViewHolder) viewHolder).mTextView.getText().toString();
            String number = ((AmbientAdapter.AmbientViewHolder) viewHolder).mNumber.getText().toString();
            ambientMap.put(ambient, number);
        }
        if(getActivity() instanceof ActionListener) {
            ((ActionListener) getActivity()).saveAmbient(ambientMap);
        }
    }
}
