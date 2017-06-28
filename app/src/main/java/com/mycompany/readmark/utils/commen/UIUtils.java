package com.mycompany.readmark.utils.commen;

import android.content.Context;
import android.content.Intent;

import com.mycompany.readmark.BaseApplication;
import com.mycompany.readmark.ui.activity.BaseActivity;

/**
 * Created by Lenovo.
 */

public class UIUtils {


    /**
     * 获取Context
     * @return
     */
    public static Context getContext(){
        return BaseApplication.getApplication();

    }


    /**
     * 启动Activity
     * @param intent
     */
    public static void startActivity(Intent intent){
        if(BaseActivity.activity == null){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }else{
            BaseActivity.activity.startActivity(intent);
        }
    }

}