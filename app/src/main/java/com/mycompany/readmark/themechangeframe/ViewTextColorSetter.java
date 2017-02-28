package com.mycompany.readmark.themechangeframe;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

/**
 * 只能用于TextView
 */
public class ViewTextColorSetter extends ViewSetter{


    public ViewTextColorSetter(int viewId, int attrId){
        super(viewId, attrId);
    }

    public ViewTextColorSetter(View target, int attrId){
        super(target, attrId);
    }


    @Override
    public void setValue(Resources.Theme theme, int themeId) {
        if(mTargetView != null){
            ((TextView) mTargetView).setTextColor(getColor(theme));
        }
    }
}
