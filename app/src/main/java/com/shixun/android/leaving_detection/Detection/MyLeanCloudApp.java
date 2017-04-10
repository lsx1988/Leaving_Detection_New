package com.shixun.android.leaving_detection.Detection;

import com.avos.avoscloud.AVOSCloud;

import org.litepal.LitePalApplication;

/**
 * Created by shixunliu on 8/4/17.
 */

public class MyLeanCloudApp extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"n78FocbJYXyhGdqpHoOkJ7KK-gzGzoHsz","KYyyoF7acqDWRUza8GWgLAs9");
        //AVOSCloud.setDebugLogEnabled(true);
    }
}
