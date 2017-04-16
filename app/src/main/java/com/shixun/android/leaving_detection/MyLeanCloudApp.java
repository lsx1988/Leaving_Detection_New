package com.shixun.android.leaving_detection;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

import org.litepal.LitePal;

/**
 * Created by shixunliu on 10/4/17.
 */

public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"n78FocbJYXyhGdqpHoOkJ7KK-gzGzoHsz","KYyyoF7acqDWRUza8GWgLAs9");
        // 初始化 litepal 本地数据库
        LitePal.initialize(this);
    }
}
