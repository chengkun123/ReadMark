package com.mycompany.readmark.themechangeframeV2.skin.attr;

import android.view.View;

/**
 * Created by Lenovo.
 */

public class SkinAttr {
    private String mResName;
    private SkinType mType;

    public SkinAttr(String resName, SkinType skinType) {
        mResName = resName;
        mType = skinType;
    }

    public void skin(View view) {
        mType.skin(view, mResName);
    }
}
