package com.test.js.chat;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by lenovo on 2017-07-07.
 */

public class MyApplication extends Application { //스테소(SQLite 내부 데이터 확인) 사용하기 위해서 필요
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}

