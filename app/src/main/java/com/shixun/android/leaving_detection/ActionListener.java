package com.shixun.android.leaving_detection;

/**
 * Created by shixunliu on 17/4/17.
 */

public interface ActionListener {

    void onRegisterClick();
    void onUpNevigationClick();
    void onMenuClick();
    void onLoginSuccessful();
    void onRemodel();
    void onDetection();
    void onRegisterSuccessful();
    void startDetection(boolean isRemodel);
    void stopDetection();
    void onChooseCustomModel();
    void onChooseDefaultModel();
}
