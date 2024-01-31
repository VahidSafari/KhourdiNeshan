package com.safari.khourdineshan;

import android.app.Application;

import com.safari.khourdineshan.di.ApplicationProvider;

public class KhoordiNeshanApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationProvider.init(this);
    }

}
