package com.mycompany.readmark.themechangeframe;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lenovo on 2017/2/16.
 */
public final class ThemeChanger {

    Builder builder;

    private ThemeChanger(Builder builder){
        this.builder = builder;
    }
    public void setTheme(int themeId){
        builder.setTheme(themeId);
    }


    /*
    * 用于添加添加ViewSetter、实现主题切换
    * */
    public static class Builder{
        Set<ViewSetter> mSetters = new HashSet<ViewSetter>();
        Activity mActivity;

        public Builder(Activity activity){
            mActivity = activity;
        }

        private View findViewById(int viewId){
            return mActivity.findViewById(viewId);
        }

        public Builder addSchemedBgColorSetter(int viewId, int attrId){
            Log.e("添加了一个需要设置背景的View", "......");
            View view = findViewById(viewId);
            Log.e("找到了需要的View", (view != null) + "");
            ViewBgColorSetter vs = new ViewBgColorSetter(view, attrId);
            mSetters.add(vs);
            return this;
        }

        public Builder addSchemedTextColorSetter(int viewId, int attrId){
            mSetters.add(new ViewTextColorSetter((TextView)findViewById(viewId), attrId));
            return this;
        }

        public Builder addSchemedSetter(ViewSetter setter){
            mSetters.add(setter);
            return this;
        }

        protected void setTheme(int themeId){
            //先改变主题，然后从主题中获取值
            mActivity.setTheme(themeId);
            makeChange(themeId);
        }

        private void makeChange(int themeId){
            Resources.Theme curTheme = mActivity.getTheme();
            for(ViewSetter setter : mSetters){
                Log.e("进行了一次View的设置", "....");
                setter.setValue(curTheme, themeId);
            }
        }

        public ThemeChanger create(){
            return new ThemeChanger(this);
        }
    }
}
