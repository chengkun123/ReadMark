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
    }

    @Override
    public void setValue(Resources.Theme theme, int themeId) {
        if(mTargetView != null){
            mTargetView.setBackgroundColor(getColor(theme));
        }
    }
}
