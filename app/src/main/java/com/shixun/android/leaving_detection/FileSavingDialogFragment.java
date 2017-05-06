package com.shixun.android.leaving_detection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shixunliu on 1/5/17.
 */

public class FileSavingDialogFragment extends DialogFragment {

    @BindView(R.id.btn_dialog_ok)
    Button mButtonOk;
    @BindView(R.id.btn_dialog_cancel)
    Button mButtonCencel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View view = inflater.inflate(R.layout.fragment_file_saving_dialog, container, false);
        ButterKnife.bind(this, view);
        setCancelable(false);//点击 dialog 以外区域不会关闭 dialog
        return view;
    }

    @OnClick(R.id.btn_dialog_ok)
    public void clickOk() {
        setCancelable(true);//dialog 可关闭
        this.dismiss();//关闭dialog
    }

    @OnClick(R.id.btn_dialog_cancel)
    public void clickCancel() {
        setCancelable(true);//dialog 可关闭
        this.dismiss();//关闭dialog
    }
}
