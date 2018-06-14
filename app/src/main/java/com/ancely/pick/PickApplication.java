package com.ancely.pick;

import android.app.Application;


/*
 *  @项目名：  PickView 
 *  @包名：    com.ancely.pick
 *  @文件名:   PickApplication
 *  @创建者:   fanlelong
 *  @创建时间:  2018/6/13 上午11:33
 */

public class PickApplication extends Application {
    private static PickApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static PickApplication getInstance() {
        return mApplication;
    }
}
