package com.mycompany.readmark.themechangeframe;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Lenovo on 2017/2/16.
 */
public abstract class ViewSetter implements Comparable {
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
    }

    public abstract void setValue(Resources.Theme theme, int themeId);

    //通过属性id得到某Theme下的具体颜色值
    protected int getColor(Resources.Theme theme){
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(mAttrId, typedValue, true);
        return typedValue.data;
    }

    public View getTargetView() {
        return mTargetView;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.hashCode() - o.hashCode();
    }
}
