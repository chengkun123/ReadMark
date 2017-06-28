package com.mycompany.readmark.themechangeframe;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;


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

        /*
        * 添加一个设置BG的Setter
        * */
        public Builder addSchemedBgColorSetter(int viewId, int attrId){
            View view = findViewById(viewId);
            ViewBgColorSetter vs = new ViewBgColorSetter(view, attrId);
            mSetters.add(vs);
            return this;
        }

        /*
        * 添加一个设置TextColor的Setter
        * */
        public Builder addSchemedTextColorSetter(int viewId, int attrId){
            mSetters.add(new ViewTextColorSetter((TextView)findViewById(viewId), attrId));
            return this;
        }
        /*
        * 添加一个设置setter
        * */
        public Builder addSchemedSetter(ViewSetter setter){
            mSetters.add(setter);
            return this;
        }

        protected void setTheme(int themeId){
            //先改变主题，然后从主题中获取值
            mActivity.setTheme(themeId);
            makeChange(themeId);
        }

        /*
        * 遍历所有Setter并调用其setValue方法进行属性的改变
        *
        * */
        private void makeChange(int themeId){
            Resources.Theme curTheme = mActivity.getTheme();
            for(ViewSetter setter : mSetters){
                setter.setValue(curTheme, themeId);
            }
        }

        public ThemeChanger create(){
            return new ThemeChanger(this);
        }
    }
}
