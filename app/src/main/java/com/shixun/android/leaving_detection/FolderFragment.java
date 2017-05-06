package com.shixun.android.leaving_detection;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shixun.android.leaving_detection.Detection.svm_scale;
import com.shixun.android.leaving_detection.Detection.svm_train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shixunliu on 1/5/17.
 */

public class FolderFragment extends GeneralFragment {

    private RecyclerView mRecyclerView = null;
    private FileAdapter mFileAdapter;

    @BindView(R.id.newton_cradle_loading)
    com.victor.loading.newton.NewtonCradleLoading mRotateLoading;

    @Override
    public void onResume() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("File List");
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_list, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_file_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFileAdapter = new FileAdapter();

        mRecyclerView.setAdapter(mFileAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFileAdapter.setClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onClick(List<File> fileList, int position, String category) {
                File file = fileList.get(position);
                if(getActivity() instanceof ActionListener) {
                    if(category.equals("file")) {
                        ((ActionListener) getActivity()).onShowFileText(file);
                    } else {
                        mRotateLoading.setVisibility(View.VISIBLE);
                        mRotateLoading.start();
                        getScalePara(file);
                        getModel();
                        mRotateLoading.stop();
                        mRotateLoading.setVisibility(View.GONE);
                    }
                }

            }
        });

        FetchFile();
        return view;
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

    private void getScalePara(File file) {
        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();

            // 在 SD 卡的根目录下创建文件夹
            File folder =  new File(sdCardPath + File.separator + "Scale_Para");

            if(!folder.exists()) {
                folder.mkdir();
            }

            String[] args = {"-s",folder.toString()+"/scale_para.txt"};

            removeOldTrainScaledData();

            svm_scale scale = new svm_scale();
            try {
                scale.main(args, file);
            } catch (IOException e) {

            }
        }
    }

    private void removeOldTrainScaledData() {
        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();

            // 在 SD 卡的根目录下创建文件夹
            File folder = new File(sdCardPath + File.separator + "Data_Scaled");

            File file = new File(folder, "/train_scaled.txt");

            if(file.exists()) {
                file.delete();
            }
        }
    }

    private void getModel() {
        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();

            // 在 SD 卡的根目录下创建文件夹
            File folder = new File(sdCardPath + File.separator + "Data_Scaled");

            File file = new File(folder, "/train_scaled.txt");

            if(file.exists()) {
                String[] args = {"-b","1","-c","32","-g","0.5"};
                try {
                    svm_train.main(args, file);
                } catch (IOException e) {

                }
            }
        }
    }
}
