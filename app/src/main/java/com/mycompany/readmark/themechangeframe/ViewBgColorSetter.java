package com.mycompany.readmark.themechangeframe;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;

/**
 * Created by Lenovo on 2017/2/16.
 */
public class ViewBgColorSetter extends ViewSetter{

    public ViewBgColorSetter(int viewId, int attrId){
        super(viewId, attrId);
    }

    public ViewBgColorSetter(View target, int attrId){
        super(target, attrId);
        Log.e("调用了构造器", "此时view不空" + (target != null));
    }

    @Override
    public void setValue(Resources.Theme theme, int themeId) {
        Log.e("View为空",(mTargetView == null) + "");
        if(mTargetView != null){
            Log.e("View不为空","进行了颜色设置");
            mTargetView.setBackgroundColor(getColor(theme));
        }
    }
}
