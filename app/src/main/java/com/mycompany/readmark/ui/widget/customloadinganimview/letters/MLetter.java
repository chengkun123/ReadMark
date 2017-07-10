package com.mycompany.readmark.ui.widget.customloadinganimview.letters;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.mycompany.readmark.ui.widget.customloadinganimview.Config;


/**
 * Created by Lenovo.
 */

public class MLetter extends Letter {

    private Point mFirPoint;
    private Point mSecPoint;
    private Point mThiPoint;
    private Point mFourPoint;

    private Paint mPaint;

    private ValueAnimator mAnimator;

    public MLetter(int x, int y) {
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

        mFirPoint = new Point(x, y - 2 * MAX_RADIUS_CIRCLE);
        mSecPoint = new Point(mFirPoint);
        mThiPoint = new Point(mFirPoint);
        mFourPoint = new Point(mFirPoint);
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
                mSecPoint.x = (int) (mFirPoint.x + 3 * MAX_RADIUS_CIRCLE * factor);
                mFourPoint.y = (int) (mThiPoint.y + 3 * MAX_RADIUS_CIRCLE * factor);
            }
        });
        mAnimator.start();
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawLine(mFirPoint.x, mFirPoint.y, mSecPoint.x, mSecPoint.y, mPaint);
        canvas.drawLine(mThiPoint.x, mThiPoint.y, mFourPoint.x, mFourPoint.y, mPaint);
        canvas.drawLine(mThiPoint.x + MAX_RADIUS_CIRCLE * 3 / 2
                , mThiPoint.y
                , mFourPoint.x+ MAX_RADIUS_CIRCLE * 3 / 2
                , mFourPoint.y
                , mPaint);
        canvas.drawLine(mThiPoint.x+ 3 * MAX_RADIUS_CIRCLE, mThiPoint.y, mFourPoint.x+ 3 * MAX_RADIUS_CIRCLE, mFourPoint.y, mPaint);

    }
}
