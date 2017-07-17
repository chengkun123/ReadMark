package com.mycompany.readmark.themechangeframeV2.skin.attr;

import android.view.View;

import java.util.List;

/**
 * Created by Lenovo.
 */

public class SkinView {

    private View mView;

    private List<SkinAttr> mAttrs;

    public SkinView(View view, List<SkinAttr> skinArrs) {
        mAttrs = skinArrs;
        mView = view;

    }

    /**
     * 对这个View的所有attr进行换肤
     *
     */
    public void skin(){
        for (SkinAttr attr : mAttrs){
            attr.skin(mView);
        }
    }

    public View getView() {
        return mView;
    }
}
