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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shixunliu on 1/5/17.
 */

public class AddNewAmbientDialog extends DialogFragment {

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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//remove dialog title
        View view = inflater.inflate(R.layout.dialog_type_in_name, container, false);
        ButterKnife.bind(this, view);
        ambientName.setHint(getResources().getString(R.string.new_ambient_dialog_hint));
        mComment.setText(getResources().getString(R.string.new_ambient_dialog_title));
        setCancelable(false);//dialog will not close when click outside area
        return view;
    }

    @OnClick(R.id.btn_dialog_ok)
    public void clickOk() {
        String name = ambientName.getText().toString();
        if(name.equals("")) {
            showSnackBar(getResources().getString(R.string.no_blank_name), R.drawable.icon_alert);
        } else {
            ((AmbientListFragment) getTargetFragment()).addNewAmbient(name);
            showSnackBar(getResources().getString(R.string.add_ambient_ok),R.drawable.icon_success);
            setCancelable(true);//dialog is cancelable
            this.dismiss();//close dialog
        }
    }

    @OnClick(R.id.btn_dialog_cancel)
    public void clickCancel() {
        setCancelable(true);
        this.dismiss();
    }

    private void showSnackBar(String msg, int iconId) {
        Snackbar snackbar = Snackbar.make(getParentFragment().getView(), msg, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
        snackbar.setDuration(3000).show();
    }
}
