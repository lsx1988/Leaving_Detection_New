package com.shixun.android.leaving_detection.Activity;

import android.os.Bundle;

import java.io.File;
import java.util.HashMap;

/**
 * Created by shixunliu on 17/4/17.
 */

public interface ActionListener {

    void onUpNevigationClick();
    void onMenuClick();
    void onRemodel();
    void startService();
    void stopService();
    void onCollectSensorDataSuccessful();
    void onShowFileText(File file);
    void onSendDataBack(Bundle bundle);
    void showModelTypeDialog();
    void startMain(File file);
    void updateSensor(File file);
    void addNewTrainingData();
    void addNewModel();
    void saveAmbient(HashMap<String, String> ambientMap);
}
