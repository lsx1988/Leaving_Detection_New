package com.shixun.android.leaving_detection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shixunliu on 17/4/17.
 */

public class ModelOptionDialogFragment extends DialogFragment {

    @BindView(R.id.rb_custom_model)
    RadioButton mCustomButton;
    @BindView(R.id.rb_default_model)
    RadioButton mDefaultButton;
    @BindView(R.id.btn_dialog_ok)
    Button mButtonOk;
    @BindView(R.id.dialog_comment)
    TextView mComment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View view = inflater.inflate(R.layout.fragment_model_option_dialog, container, false);
        ButterKnife.bind(this, view);
        setCancelable(false);//点击 dialog 以外区域不会关闭 dialog
        return view;
    }

    @OnClick(R.id.rb_default_model)
    public void defautClick() {
        mComment.setText(getResources().getText(R.string.comment_default));
    }

    @OnClick(R.id.rb_custom_model)
    public  void customClick() {
        mComment.setText(getResources().getText(R.string.comment_custom));
    }

    @OnClick(R.id.btn_dialog_ok)
    public void clickOk() {

        setCancelable(true);//dialog 可关闭
        this.dismiss();//关闭dialog

        if(getActivity() instanceof ActionListener) {
            if(mCustomButton.isChecked()) {
                AVUser.getCurrentUser().put("DefaultModel", false);
                AVUser.getCurrentUser().put("CustomModel", true);
                AVUser.getCurrentUser().saveInBackground();
                ((ActionListener) getActivity()).onChooseCustomModel();
            } else {
                AVUser.getCurrentUser().put("pressure", false);
                AVUser.getCurrentUser().put("magnetic", false);
                AVUser.getCurrentUser().put("wifi", true);
                AVUser.getCurrentUser().put("DefaultModel", true);
                AVUser.getCurrentUser().put("CustomModel", false);
                AVUser.getCurrentUser().saveInBackground();
                ((ActionListener) getActivity()).onChooseDefaultModel();
            }
        }
    }
}
