package com.mycompany.readmark.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Lenovo on 2016/11/22.
 */
public class BaseApplication extends Application {
    public static Context sAppContext;

    public void onCreate(){
        super.onCreate();
        sAppContext = getApplicationContext();
    }
}
