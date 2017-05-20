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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_type_in_name, container, false);
        ButterKnife.bind(this, view);
        setCancelable(false);
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
        showSnackBar(getString(R.string.model_added_success));
        setCancelable(true);
        this.dismiss();
    }

    @OnClick(R.id.btn_dialog_cancel)
    public void clickCancel() {
        setCancelable(true);
        this.dismiss();
    }

    private void createScalePara(File file, String folderName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardPath = Environment.getExternalStorageDirectory();

            File folder =  new File(sdCardPath + File.separator + getString(R.string.model_folder));
            if(!folder.exists()) {
                folder.mkdir();
            }
            folder =  new File(folder + File.separator + folderName);
            if(!folder.exists()) {
                folder.mkdir();
            }

            String[] args = {"-s",folder.toString()+ File.separator + getString(R.string.scale_para_file_name)};
            removeOldTrainScaledData();
            svm_scale_Remodel scale = new svm_scale_Remodel();
            try {
                scale.main(args, file);
            } catch (IOException e) {

            }
        }
    }

    private void removeOldTrainScaledData() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardPath = Environment.getExternalStorageDirectory();
            File folder = new File(sdCardPath + File.separator +  getString(R.string.scaled_train_data_folder));
            File file = new File(folder + File.separator + getString(R.string.scaled_train_data_file_name));
            if(file.exists()) {
                file.delete();
            }
        }
    }

    private void createModel(String folderName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardPath = Environment.getExternalStorageDirectory();
            File folder = new File(sdCardPath + File.separator + getString(R.string.scaled_train_data_folder));
            File file = new File(folder + File.separator + getString(R.string.scaled_train_data_file_name));
            if(file.exists()) {
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
            File folder =  new File(sdCardPath + File.separator + getString(R.string.model_folder));
            if(!folder.exists()) {
                folder.mkdir();
            }
            folder =  new File(folder + File.separator + folderName);
            if(!folder.exists()) {
                folder.mkdir();
            }
            File file = new File(folder + File.separator + getString(R.string.model_file_name));
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
