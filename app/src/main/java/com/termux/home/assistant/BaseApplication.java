package com.termux.home.assistant;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
    public static Context baseContext;
    @Override
    public void onCreate() {
        super.onCreate();
        baseContext=this;
    }
}
