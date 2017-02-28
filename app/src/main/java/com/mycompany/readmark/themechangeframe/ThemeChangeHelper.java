package com.mycompany.readmark.themechangeframe;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 主题切换帮助类，用于保存/获取模式信息
 */
public class ThemeChangeHelper {
    private final static String FILE_NAME = "theme_settings";
    private final static String MODE = "theme_mode";

    private SharedPreferences mSharedPreferences;

    public ThemeChangeHelper(Context context){
        mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public boolean setMode(DayNight dayNight){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(MODE, dayNight.getName());
        return editor.commit();
    }

    public boolean isDay(){
        String modeName = mSharedPreferences.getString(MODE, DayNight.DAY.getName());
        if(modeName.equals(DayNight.DAY.getName())){
            return true;
        }
        return false;
    }
}
