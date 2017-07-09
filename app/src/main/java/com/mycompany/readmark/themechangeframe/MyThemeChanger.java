package com.mycompany.readmark.themechangeframe;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Lenovo.
 */

public class MyThemeChanger {
    private static volatile MyThemeChanger sMyThemeChanger;
    private Set<ViewSetter> mViewSetters;
    private Context mContext;

    public static MyThemeChanger getMyThemeChanger(Context context){
        if(sMyThemeChanger == null){
            synchronized (MyThemeChanger.class){
                if(sMyThemeChanger == null){
                    sMyThemeChanger = new MyThemeChanger(context);
                }
            }
        }
        return sMyThemeChanger;
    }

    private MyThemeChanger(Context context){
        mViewSetters = new ConcurrentSkipListSet<>();
        mContext = context;
    }

    public void addViewSetter(ViewSetter setter){
        mViewSetters.add(setter);
        Log.e("addViewSetter", "添加了一个");
        Log.e("mViewSetters size", ""+mViewSetters.size());
    }

    public void deleteViewSetter(View view){
        for(ViewSetter setter : mViewSetters){
            /*if(setter.getTargetView().getParent() == view){
                Log.e("deleteViewSetter", "删除了一个");
                mViewSetters.remove(setter);
            }
            if(setter.getTargetView().getContext() == mContext){
                Log.e("deleteViewSetter", "删除了一个");
                mViewSetters.remove(setter);
            }*/
            mViewSetters.remove(setter);
        }
        Log.e("mViewSetters size", ""+mViewSetters.size());
    }

    public void setTheme(int themeId){
        mContext.setTheme(themeId);
        makeChange(themeId);
    }

    private void makeChange(int themeId){
        Resources.Theme theme = mContext.getTheme();
        for(ViewSetter setter : mViewSetters){
            setter.setValue(theme, themeId);
        }

    }
}
