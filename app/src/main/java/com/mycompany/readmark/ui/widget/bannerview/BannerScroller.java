package com.mycompany.readmark.ui.widget.bannerview;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by Lenovo.
 */

public class BannerScroller extends Scroller {

    //固定时间，切换速率
    private int mScrollerDuaration = 1000;

    public void setScrollerDuaration(int scrollerDuaration) {
        mScrollerDuaration = scrollerDuaration;
    }

    public BannerScroller(Context context) {
        super(context);
    }

    public BannerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public BannerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duaration) {
        //传入固定duaration
        super.startScroll(startX, startY, dx, dy, mScrollerDuaration);
    }
}
