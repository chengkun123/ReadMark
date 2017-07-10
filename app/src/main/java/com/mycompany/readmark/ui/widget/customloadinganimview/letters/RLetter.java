package com.mycompany.readmark.ui.widget.customloadinganimview.letters;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.mycompany.readmark.ui.widget.customloadinganimview.Config;


/**
 * Created by Lenovo.
 */

public class RLetter extends Letter {

    private Paint mPaint;


    private int mCircleX;
    private int mCircleY;

    private Point mFirstPoint;
    private Point mSecondPoint;
    private Point mThirdPoint;
    private Point mFourthPoint;
    private Point mFifthPoint;

    private RectF mRectF;

    private int mLength1;
    private int mLength2;

    private ValueAnimator mAnimator;
    private int mSweepArg;

    public RLetter(int x, int y) {
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

        mLength1 = 3 * MAX_RADIUS_CIRCLE;
        //mLength2 = (int) Math.sqrt(MAX_RADIUS_CIRCLE * MAX_RADIUS_CIRCLE + MAX_RADIUS_CIRCLE * MAX_RADIUS_CIRCLE);

        mCircleX = x;
        mCircleY = y - MAX_RADIUS_CIRCLE;

        mRectF = new RectF(mCircleX - MAX_RADIUS_CIRCLE
                , mCircleY - MAX_RADIUS_CIRCLE
                , mCircleX + MAX_RADIUS_CIRCLE
                , mCircleY + MAX_RADIUS_CIRCLE);


        mFirstPoint = new Point(x - MAX_RADIUS_CIRCLE
                , y - 2 * MAX_RADIUS_CIRCLE);
        mSecondPoint = new Point(mFirstPoint);

        mThirdPoint = new Point(x - MAX_RADIUS_CIRCLE, y + MAX_RADIUS_CIRCLE / 4);
        mFourthPoint = new Point(mThirdPoint);
        mFifthPoint = new Point(mThirdPoint);
    }

    @Override
    public void startAnim() {
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(mDuration);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                mSweepArg = (int) (360 * factor);
                mSecondPoint.y = (int) (mFirstPoint.y + mLength1 * factor);
                if(factor > 0.6 && factor <= 0.9){
                    mFourthPoint.x = (int) (mThirdPoint.x + 2 * MAX_RADIUS_CIRCLE * (factor - 0.6) / 3 * 10);
                    mFifthPoint.x = mFourthPoint.x;
                }else if(factor > 0.9){
                    mFifthPoint.y = mSecondPoint.y;
                }

            }
        });
        mAnimator.start();
    }

    /**
     /* @param canvas
     */
    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawArc(mRectF, -180, mSweepArg, false, mPaint);

        //长竖线
        canvas.drawLine(mFirstPoint.x
                , mFirstPoint.y
                , mSecondPoint.x
                , mSecondPoint.y
                , mPaint);
        //短横线
        canvas.drawLine(mThirdPoint.x
                , mThirdPoint.y
                , mFourthPoint.x
                , mFourthPoint.y
                , mPaint);
        //短竖线
        canvas.drawLine(mFourthPoint.x
                , mFourthPoint.y
                , mFifthPoint.x
                , mFifthPoint.y, mPaint);

    }
}
