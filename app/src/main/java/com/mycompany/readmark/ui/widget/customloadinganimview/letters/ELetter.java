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

public class ELetter extends Letter {
    private Paint mPaint;


    private ValueAnimator mAnimator;
    private RectF mRectF;
    private Point mFirstPoint;
    private Point mSecondPoint;

    private int mSweepArg;


    public ELetter(int x, int y) {
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
                , y - MAX_RADIUS_CIRCLE
                , x + MAX_RADIUS_CIRCLE
                , y + MAX_RADIUS_CIRCLE);
        mFirstPoint = new Point(x - MAX_RADIUS_CIRCLE, y);
        mSecondPoint = new Point(mFirstPoint);
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
                if(factor < 0.25){
                    mSecondPoint.x = (int) (mFirstPoint.x + 2 * MAX_RADIUS_CIRCLE * factor * 4);
                }else if(factor >= 0.25){
                    mSweepArg = (int) (310 * (factor - 0.25) / 3 * 4);
                }


            }
        });
        mAnimator.start();
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawLine(mFirstPoint.x, mFirstPoint.y, mSecondPoint.x, mSecondPoint.y, mPaint);

        canvas.drawArc(mRectF, 0, -mSweepArg, false, mPaint);
    }
}
