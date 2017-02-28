package com.mycompany.readmark.themechangeframe;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Lenovo on 2017/2/16.
 */
public abstract class ViewSetter {
    protected View mTargetView;
    protected int mTargetViewId;
    protected int mAttrId;

    public ViewSetter(int targetViewId, int attrId){
        mTargetViewId = targetViewId;
        mAttrId = attrId;
    }

    public ViewSetter(View targetView, int attrId){
        mTargetView = targetView;
        mAttrId = attrId;
        Log.e("父类构造器，此时View不空",(targetView != null)+"");
    }

    public abstract void setValue(Resources.Theme theme, int themeId);

    //通过属性id得到某Theme下的具体属性值（这里是获得颜色）
    protected int getColor(Resources.Theme theme){
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(mAttrId, typedValue, true);
        return typedValue.data;
    }
}
