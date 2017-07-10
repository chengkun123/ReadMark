package com.mycompany.readmark.ui.widget.customloadinganimview.cakes;

import android.graphics.Canvas;

/**
 * Created by Lenovo.
 */

public abstract class Cake {
    protected final static int MAX_RADIUS_CIRCLE = 60;

    protected int mCurX;
    protected int mCurY;

    protected int mPrimitiveX;
    //结束动画时长
    protected int mEndDuration = 4000;

    protected boolean isChangeEnd = false;
    protected boolean isEndAnimStart = false;


    //结束后移动距离,需要移动到的距离分别是-340，-200， -60， 300
    //本身分别在-210， -70， 70， 210
    protected int mEndMovingLength;


    protected AnimatorStateListener mListener;

    public Cake(int curX, int curY) {
        mCurX = curX;
        mCurY = curY;
        mPrimitiveX = curX;
    }

    public int getCurX() {
        return mCurX;
    }

    public int getCurY() {
        return mCurY;
    }

    protected abstract void initConfig();

    protected abstract void initAnim();

    protected abstract void initEndAnim();

    public void prepareAnim(){
        initConfig();
        initAnim();
        initEndAnim();
    }

    public abstract void startAnim();

    //改变标志位，因为在不断重绘，所以会进入相应的绘制阶段
    public void endAnim(){
        isChangeEnd = true;
        isEndAnimStart = false;
    }

    public abstract void drawSelf(Canvas canvas);


    public interface AnimatorStateListener{
        void onCakeChangeAnimatorEnd();
        void onMoveEnd();
        void onAllAnimatorEnd();
    }

    public void setAnimatorStateListener(AnimatorStateListener listener){
        mListener = listener;
    }
}
