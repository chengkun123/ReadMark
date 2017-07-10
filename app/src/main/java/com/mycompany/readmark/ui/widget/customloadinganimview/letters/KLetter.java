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

public class KLetter extends Letter{

    private Paint mPaint;

    private Point mFirPoint;
    private Point mSecPoint;
    private Point mThiPoint;
    private Point mFourPoint;
    private Point mFifPoint;

    private RectF mRectF;
    private int mSweepArg;

    private ValueAnimator mAnimator;

    public KLetter(int x, int y) {
        super(x, y);


    }

    @Override
    protected void initConfig(int x, int y) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Config.WHITE);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);

        mRectF = new RectF(x - MAX_RADIUS_CIRCLE
                , y - MAX_RADIUS_CIRCLE - MAX_RADIUS_CIRCLE / 2
                , x + MAX_RADIUS_CIRCLE
                , y + MAX_RADIUS_CIRCLE / 2);

        mFirPoint = new Point(x, y - 2 * MAX_RADIUS_CIRCLE);
        mSecPoint = new Point(mFirPoint);
        mThiPoint = new Point(x, y + MAX_RADIUS_CIRCLE / 2 + MAX_RADIUS_CIRCLE / 4);
        mFourPoint = new Point(mThiPoint);
        mFifPoint = new Point(mThiPoint);
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
                mSweepArg = (int) (180 * factor);
                mSecPoint.y = (int) (mFirPoint.y + 3 * MAX_RADIUS_CIRCLE * factor);
                if(factor > 0.6 && factor < 0.9){
                    mFourPoint.x = (int) (mThiPoint.x + (factor - 0.6) / 4 * 10 * MAX_RADIUS_CIRCLE);
                    mFifPoint.x = mFourPoint.x;
                }else if(factor > 0.9){
                    mFifPoint.y = mSecPoint.y;
                }
            }
        });
        mAnimator.start();
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawArc(mRectF, -90, mSweepArg, false, mPaint);
        canvas.drawLine(mFirPoint.x, mFirPoint.y, mSecPoint.x, mSecPoint.y, mPaint);
        canvas.drawLine(mThiPoint.x, mThiPoint.y, mFourPoint.x, mFourPoint.y, mPaint);
        //短竖线
        canvas.drawLine(mFourPoint.x
                , mFourPoint.y
                , mFifPoint.x
                , mFifPoint.y, mPaint);


    }
}
