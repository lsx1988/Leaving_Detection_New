package com.shixun.android.leaving_detection.Fragment;

import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shixunliu on 1/5/17.
 */

public class RawDataListFragment extends GeneralFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView = null;
    private RawDataAdapter mFileAdapter;
    private FloatingActionButton mFloatingActionButton;

    @Override
    public void onResume() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Raw Data List");
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_file_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFileAdapter = new RawDataAdapter();
        mRecyclerView.setAdapter(mFileAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFileAdapter.setClickListener(new RawDataAdapter.OnItemClickListener() {
            @Override
            public void onClick(List<File> fileList, int position, String category) {
                File file = fileList.get(position);
                if(getActivity() instanceof ActionListener) {
                    if(category.equals("file")) {
                        ((ActionListener) getActivity()).onShowFileText(file);
                    } else if(category.equals("train")) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("textFile",file);
                        NameModelAndTrainFragment fileDialog = new NameModelAndTrainFragment();
                        fileDialog.setArguments(bundle);
                        fileDialog.show(getChildFragmentManager(), "FileSavingDialog");
                    }
                }
            }
        });

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.btnFloatingAction);
        mFloatingActionButton.setOnClickListener(this);

        FetchFile();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFloatingAction:
                if(getActivity() instanceof ActionListener) {
                    ((ActionListener) getActivity()).addNewTrainingData();
                }
        }
    }

    private void FetchFile() {
        List<File> fileList = new ArrayList<>();

        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();

            // 在 SD 卡的根目录下创建文件夹
            String path = sdCardPath.toString() + File.separator + "Sensor_Data";
            File directory = new File(path);
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {
                fileList.add(files[i]);
            }

            mFileAdapter.setFileList(fileList);
        }
    }
}
