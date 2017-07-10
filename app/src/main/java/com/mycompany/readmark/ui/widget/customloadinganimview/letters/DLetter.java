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

public class DLetter extends Letter {
    private Paint mPaint;

    private RectF mRectF;

    private int mSweepAngle = 0;


    private ValueAnimator mAnimator;

    private Point mFirPoint;
    private Point mSecPoint;

    public DLetter(int x, int y) {
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
        mFirPoint = new Point(x + MAX_RADIUS_CIRCLE, y - 2 * MAX_RADIUS_CIRCLE);
        mSecPoint = new Point(mFirPoint);
    }

    @Override
    public void startAnim() {
        mAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDuration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                //竖线
                mSecPoint.y = (int) (mFirPoint.y + MAX_RADIUS_CIRCLE * 3 * factor);
                mSweepAngle = (int) (360 * factor);
            }
        });
        mAnimator.start();
    }

    @Override
    public void drawSelf(Canvas canvas) {
        //竖线
        canvas.drawLine(mFirPoint.x, mFirPoint.y, mSecPoint.x, mSecPoint.y, mPaint);
        //圆
        canvas.drawArc(mRectF, 0, - mSweepAngle, false, mPaint);
    }
}
