package com.shixun.android.leaving_detection.Fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shixun.android.leaving_detection.LibSVM.svm_scale_Remodel;
import com.shixun.android.leaving_detection.LibSVM.svm_train_Remodel;
import com.shixun.android.leaving_detection.R;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shixunliu on 1/5/17.
 */

public class NameModelAndTrainFragment extends DialogFragment {

    private File file;
    @BindView(R.id.btn_dialog_ok)
    Button mButtonOk;
    @BindView(R.id.btn_dialog_cancel)
    Button mButtonCencel;
    @BindView(R.id.progressBar)
    ProgressBar loading;
    @BindView(R.id.editText)
    EditText modelName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View view = inflater.inflate(R.layout.dialog_type_in_name, container, false);
        ButterKnife.bind(this, view);
        setCancelable(false);//点击 dialog 以外区域不会关闭 dialog
        file = (File) getArguments().getSerializable("textFile");
        return view;
    }

    @OnClick(R.id.btn_dialog_ok)
    public void clickOk() {
        loading.setVisibility(View.VISIBLE);
        String sensorInfo = getSensorInfoFromFileName(file);
        String folderName = modelName.getText() + " " + sensorInfo;
        createScalePara(file,folderName);
        createModel(folderName);
        showSnackBar("New model is added successfully");
        setCancelable(true);//dialog 可关闭
        this.dismiss();//关闭dialog
    }

    @OnClick(R.id.btn_dialog_cancel)
    public void clickCancel() {
        setCancelable(true);//dialog 可关闭
        this.dismiss();//关闭dialog
    }

    private void createScalePara(File file, String folderName) {
        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();

            // 在 SD 卡的根目录下创建文件夹
            File folder =  new File(sdCardPath + File.separator + "LeavingDetection");
            if(!folder.exists()) {
                folder.mkdir();
            }
            folder =  new File(folder + File.separator + folderName);
            if(!folder.exists()) {
                folder.mkdir();
            }

            String[] args = {"-s",folder.toString()+"/scale_para.txt"};
            removeOldTrainScaledData();
            svm_scale_Remodel scale = new svm_scale_Remodel();
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

    private void createModel(String folderName) {
        // 判断SD卡是否存在，并且本程序是否拥有SD卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获得SD卡的根目录
            File sdCardPath = Environment.getExternalStorageDirectory();
            File folder = new File(sdCardPath + File.separator + "Data_Scaled");
            File file = new File(folder, "/train_scaled.txt");

            if(file.exists()) {
                //String[] args = {"-b","1","-c","32","-g","0.5"};
                String[] args = {"-b","1"};
                try {
                    svm_train_Remodel.main(args, file, getModelFilePath(folderName));
                } catch (IOException e) {

                }
            }
        }
    }

    private String getModelFilePath(String folderName) throws IOException{
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardPath = Environment.getExternalStorageDirectory();
            // 在 SD 卡的根目录下创建文件夹
            File folder =  new File(sdCardPath + File.separator + "LeavingDetection");
            if(!folder.exists()) {
                folder.mkdir();
            }
            folder =  new File(folder + File.separator + folderName);
            if(!folder.exists()) {
                folder.mkdir();
            }
            File file = new File(folder, "/model.txt");
            return file.toString();
        } else {
            return null;
        }
    }

    private String getSensorInfoFromFileName(File file) {
        String fileName = file.toString();
        StringTokenizer st = new StringTokenizer(fileName," ");
        String date = (String) st.nextElement();
        String sensorInfo = (String) st.nextElement();
        return sensorInfo;
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(getParentFragment().getView(), msg, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_success, 0, 0, 0);
        snackbar.setDuration(3000).show();
    }
}
