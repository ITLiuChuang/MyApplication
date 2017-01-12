package com.atguigu.mobileplayer.app;

import android.app.Application;

import org.xutils.x;

/**
 * Created by 刘闯 on 2017/1/12.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
