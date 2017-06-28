package com.mycompany.readmark;

import android.app.Application;
import android.content.Context;

import com.mycompany.readmark.ui.activity.BaseActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Lenovo.
 */

public class BaseApplication extends Application{
    private final static String TAG = "BaseApplication";
    private static List<BaseActivity> activities;
    private static BaseApplication application;
    private static int mainTId;


    @Override
    public void onCreate() {
        super.onCreate();
        activities = new LinkedList<>();
        application = this;
        mainTId = android.os.Process.myTid();
    }

    /**
     * 返回Application
     *@return
     */
    public static Context getApplication(){
        return application;
    }


    /**
     * 添加一个activity
     * @param activity
     */
    public void addActivity(BaseActivity activity){
        activities.add(activity);
    }

    /**
     * 删除一个activity
     * @param activity
     */
    public void removeActivity(BaseActivity activity){
        activities.remove(activity);
    }

    /**
     * 删除所有activity
     */
    public static void removeAllActivities(){
        ListIterator<BaseActivity> iterator = activities.listIterator();
        BaseActivity activity;
        while(iterator.hasNext()){
            activity = iterator.next();
            if(activity != null){
                activity.finish();
            }
        }
    }

    /**
     * 退出应用
     */
    public static void quiteApplication(){
        removeAllActivities();
        System.exit(0);
    }

}
