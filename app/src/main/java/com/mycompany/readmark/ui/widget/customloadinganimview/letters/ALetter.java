package com.mycompany.readmark.ui.widget.customloadinganimview.letters;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.mycompany.readmark.ui.widget.customloadinganimview.Config;


/**
 * Created by Lenovo.
 */

public class ALetter extends Letter {

    private RectF mRectF;

    private Point mFirPoint;
    private Point mSecPoint;

    private Paint mPaint;

    private ValueAnimator mAnimator;
    private int mSweepArg;


    public ALetter(int x, int y) {
        super(x, y);
    }

    protected void initConfig(int x, int y){
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Config.WHITE);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);

        mRectF = new RectF(x - MAX_RADIUS_CIRCLE
                , y - MAX_RADIUS_CIRCLE
                , x + MAX_RADIUS_CIRCLE
                , y + MAX_RADIUS_CIRCLE);

        mFirPoint = new Point(x + MAX_RADIUS_CIRCLE, y - MAX_RADIUS_CIRCLE);
        mSecPoint = new Point(mFirPoint);
    }


    @Override
    public void startAnim() {
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(mDuration);
        mAnimator.setRepeatCount(0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                mSweepArg = (int) (360 * factor);
                mSecPoint.y = (int) (mFirPoint.y + 2 * MAX_RADIUS_CIRCLE * factor);

            }
        });
        mAnimator.start();
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawArc(mRectF, 0, -mSweepArg, false, mPaint);
        canvas.drawLine(mFirPoint.x, mFirPoint.y, mSecPoint.x, mSecPoint.y, mPaint);
    }
}
