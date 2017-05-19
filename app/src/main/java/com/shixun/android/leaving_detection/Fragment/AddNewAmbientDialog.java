package com.shixun.android.leaving_detection.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shixun.android.leaving_detection.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shixunliu on 1/5/17.
 */

public class AddNewAmbientDialog extends DialogFragment {

    private File file;
    @BindView(R.id.btn_dialog_ok)
    Button mButtonOk;
    @BindView(R.id.btn_dialog_cancel)
    Button mButtonCencel;
    @BindView(R.id.editText)
    EditText ambientName;
    @BindView(R.id.dialog_comment)
    TextView mComment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View view = inflater.inflate(R.layout.dialog_type_in_name, container, false);
        ButterKnife.bind(this, view);
        ambientName.setHint("Your new ambient");
        mComment.setText("PLEASE TYPE IN THE AMBIENT NAME");
        setCancelable(false);//点击 dialog 以外区域不会关闭 dialog
        return view;
    }

    @OnClick(R.id.btn_dialog_ok)
    public void clickOk() {
        String name = ambientName.getText().toString();
        if(name.equals("")) {
            showSnackBar("Name can not be blank", R.drawable.icon_alert);
        } else {
            ((AmbientListFragment) getTargetFragment()).addNewAmbient(name);
            showSnackBar("New ambient is added successfully",R.drawable.icon_success);
            setCancelable(true);//dialog 可关闭
            this.dismiss();//关闭dialog
        }
    }

    @OnClick(R.id.btn_dialog_cancel)
    public void clickCancel() {
        setCancelable(true);//dialog 可关闭
        this.dismiss();//关闭dialog
    }

    private void showSnackBar(String msg, int iconId) {
        Snackbar snackbar = Snackbar.make(getParentFragment().getView(), msg, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
        snackbar.setDuration(3000).show();
    }
}
