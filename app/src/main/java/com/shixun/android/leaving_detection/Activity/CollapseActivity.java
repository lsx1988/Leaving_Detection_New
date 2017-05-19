package com.shixun.android.leaving_detection.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shixun.android.leaving_detection.ManageActivity;
import com.shixun.android.leaving_detection.R;

/**
 * Created by shixunliu on 8/5/17.
 */

public class CollapseActivity extends Activity {
    private Button btnRestart, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collapse_activity);
        setTitle("APP Crash");
        btnRestart = (Button) findViewById(R.id.collapse_restart);
        btnExit = (Button) findViewById(R.id.collapse_exit);
        btnRestart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        ManageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
