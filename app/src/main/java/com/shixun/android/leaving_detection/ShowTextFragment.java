package com.shixun.android.leaving_detection;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import butterknife.BindView;

/**
 * Created by shixunliu on 2/5/17.
 */

public class ShowTextFragment extends GeneralFragment {

    private static final String TAG = "ShowTextFragment";
    
    @BindView(R.id.text)
    TextView mTextView;

    @Override
    public void onResume() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Current File");
        File file = (File) getArguments().getSerializable("textFile");
        try {
            showText(file, mTextView);
        } catch (IOException e) {

        }
        super.onResume();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_show_text;
    }

    private void showText(File file, TextView textView) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while((line = br.readLine())!=null){
            sb.append(line);
            sb.append("\n");
        }
        br.close();
        textView.setText(sb.toString());
    }
}
