package com.mycompany.readmark.ui.widget.customloadinganimview.letters;

import android.graphics.Canvas;

/**
 * Created by Lenovo.
 */

public abstract class Letter {
    protected final static int MAX_RADIUS_CIRCLE = 60;
    protected int mStrokeWidth = 20;
    protected int mCurX;
    protected int mCurY;

    protected int mDuration = 2000;

    public Letter(int x, int y){
        mCurX = x;
        mCurY = y;
        initConfig(x, y);
    }
    protected abstract void initConfig(int x, int y);

    public abstract void startAnim();

    public abstract void drawSelf(Canvas canvas);

}
