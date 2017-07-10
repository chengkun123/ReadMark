package com.mycompany.readmark.ui.widget.customloadinganimview.cakes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.mycompany.readmark.ui.widget.customloadinganimview.Config;

/**
 * Created by Lenovo.
 */

public class FirstCake extends Cake {

    private final static int FIRST_CIRCLE_START_RADIUS = 40;
    private final static int SECOND_CIRCLE_MAX_RADIUS = 10;
    private final static int THIRD_CIRCLE_MIN_STROKEWIDTH = 10;
    private final int DIVIDE_DEGREES = 36;

    private boolean isMoveEnd = false;

    private Paint mPaint;
    //持有float的Rect
    private RectF mRectF;


    private int mFirCirRadius;
    private int mSecCirRadius;
    private int mAroundCirDegrees;
    private int mThiCirStrokeWidth;
    private int mThiCirRadius;
    private int mAroundArcDegrees;
    private int mAroundArcLength;
    private int mPreFirCirRadius;

    private ValueAnimator mFirAnimator;
    private ValueAnimator mSecAnimator;
    private ValueAnimator mThiAnimator;
    private ValueAnimator mFouAnimator;
    private ValueAnimator mFivAnimator;


    private ValueAnimator mEndAnimator;
    private int mEndCirIRadius;
    private int mEndCirMRadius;
    private int mEndCirORadius;

    public FirstCake(int x, int y) {
        super(x, y);

    }



    protected void initConfig(){
        mEndMovingLength = -130;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mRectF = new RectF(getCurX() - 45, getCurY() - 45, getCurX() + 45, getCurY() + 45);
    }


    protected void initAnim(){

        /*
        * 第一个球半径扩大再缩小
        * */
        mFirAnimator = ValueAnimator.ofInt(FIRST_CIRCLE_START_RADIUS
                , FIRST_CIRCLE_START_RADIUS + 10
                , FIRST_CIRCLE_START_RADIUS);
        mFirAnimator.setDuration(500);
        mFirAnimator.setRepeatCount(0);
        mFirAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //Log.e("重新赋值了", (int) animation.getAnimatedValue()+"");
                mFirCirRadius = (int) animation.getAnimatedValue();
            }
        });
        mFirAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                /*mThiCirRadius = 0;
                mThiCirStrokeWidth = 0;
                mPreFirCirRadius = 0;*/
                mSecAnimator.start();
            }
        });

        /*
        * 卫星出现并环绕消失
        * */
        mSecAnimator = ValueAnimator.ofFloat(0, 1);
        mSecAnimator.setDuration(2000);
        mSecAnimator.setRepeatCount(0);
        mSecAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                mAroundCirDegrees = (int) (180 * factor);
                if(factor < 0.5){
                    mSecCirRadius = (int) (factor * 2 * SECOND_CIRCLE_MAX_RADIUS);
                }else{
                    mSecCirRadius = (int) ((1 - factor) * 2 * SECOND_CIRCLE_MAX_RADIUS);
                }
            }
        });
        mSecAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mThiAnimator.start();
                mFouAnimator.start();
            }
        });
        /*
        * 黄圈出现时，第一个球半径同时缩小
        * */
        mThiAnimator = ValueAnimator.ofInt(FIRST_CIRCLE_START_RADIUS, 20)
                .setDuration(1000);
        mThiAnimator.setRepeatCount(0);
        mThiAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mFirCirRadius = (int) animation.getAnimatedValue();
                    }
                }
        );

        /*
        * 黄圈半径缩小，stroke增大
        * */
        mFouAnimator = ValueAnimator.ofFloat(0, 1);
        mFouAnimator.setDuration(1000);
        mFouAnimator.setRepeatCount(0);
        mFouAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //50~20,实际取25~10
                mThiCirRadius = (int) (20 + 80 * (1 - (float) animation.getAnimatedValue()));
                //这里要让蓝圆缩小的速度和黄圈内边缩小的速度相等。（当然也可以计算好后简单覆盖）
                //10~20，入侵半径的范围是5~10 ,最终得到一个半径为20的实心黄圆。
                mThiCirStrokeWidth = 10 + (int) ((float) animation.getAnimatedValue()
                        * THIRD_CIRCLE_MIN_STROKEWIDTH);
            }
        });
        mFouAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFivAnimator.start();
                mAroundArcLength = 0;
            }
        });
        /*
        * 扇形环绕 + 最后一个蓝圆扩大到初始蓝圆大小
        *
        * */
        mFivAnimator = ValueAnimator.ofFloat(0.0f, 0.5f, 1, 2);
        mFivAnimator.setDuration(2000);
        mFivAnimator.setRepeatCount(0);
        mFivAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mFivAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                if(factor > 0.0 && factor <= 0.5){
                    mAroundArcDegrees = (int) (-120 + 880 * factor);
                    factor = factor * 2;
                    mAroundArcLength = (int) (180 * factor);
                }else if(factor > 0.5 && factor <= 1.0){
                    mAroundArcDegrees = (int) (-120 + 880 * factor);
                    factor = (1 - factor) * 2;
                    mAroundArcLength = (int) (180 * factor);
                } else{
                    mAroundArcLength = 0;
                    factor = factor - 1;
                    mPreFirCirRadius = (int) (FIRST_CIRCLE_START_RADIUS * factor);
                }

            }
        });
        mFivAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(mListener != null){
                    mListener.onCakeChangeAnimatorEnd();
                }
            }
        });

    }

    //第一阶段是移动，第二阶段和Letters的动画吻合，由FirstCake控制所有动画结束时的回调
    public void initEndAnim(){
        mEndAnimator = ValueAnimator.ofFloat(0, 1, 2).setDuration(mEndDuration);
//      mEndAnimator.setRepeatCount(2);
        mEndAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float zoroToOne = (float) animation.getAnimatedValue();
                if (zoroToOne <= 1.0f) {
                    mCurX = (int) (mPrimitiveX + zoroToOne * mEndMovingLength);
                    mEndCirIRadius = (int) (MAX_RADIUS_CIRCLE * zoroToOne);
                    if (zoroToOne <= 0.5f) {
                        zoroToOne = 2 * zoroToOne;
                    } else {
                        zoroToOne = 1 - 2 * (zoroToOne - 0.5f);
                    }
                    mEndCirMRadius = (int) (MAX_RADIUS_CIRCLE * zoroToOne);
                } else {
                    if (!isMoveEnd) {
                        isMoveEnd = true;
                        if (mListener != null) {
                            mListener.onMoveEnd();
                        }
                    }
                    zoroToOne = 2 - zoroToOne;
                    mEndCirIRadius = (int) (MAX_RADIUS_CIRCLE * zoroToOne);
                    if (zoroToOne >= 0.5f) {
                        zoroToOne = (1.0f - zoroToOne) * 2;
                    } else {
                        zoroToOne = zoroToOne * 2;
                    }
                    mEndCirORadius = (int) (MAX_RADIUS_CIRCLE * zoroToOne);
                }
            }
        });
        mEndAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onAllAnimatorEnd();
                }
            }
        });
    }


    public void startAnim(){
        mFirAnimator.start();
    }


    public void drawSelf(Canvas canvas){
        if(!isMoveEnd){
            /*
            * 画球
            * 正在改变的属性：
            * mFirCirRadius
            * */
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(mFirCirRadius);
            canvas.drawCircle(getCurX()
                    , getCurY()
                    , mFirCirRadius / 2, mPaint);

            /*
            * 画一圈原点
            * 正在改变的属性：
            * mSecCirRadius
            * mAroundCirDegrees
            * */
            mPaint.setColor(Config.GREEN);
            mPaint.setStrokeWidth(mSecCirRadius);
            for(int i=0; i<10; i++){
                canvas.save();
                canvas.rotate(i * 36 + mAroundCirDegrees, getCurX(), getCurY());
                canvas.drawCircle(getCurX(), getCurY() - 60, mSecCirRadius / 2, mPaint);
                canvas.restore();
            }

            /*
            * 画黄色圆环，半径在缩小，圆环宽度却在增大
            * 正在改变的属性：
            * mThiCirStrokeWidth
            * mThiCirRadius
            * */
            mPaint.setColor(Config.YELLOW);
            mPaint.setStrokeWidth(mThiCirStrokeWidth);
            canvas.drawCircle(getCurX()
                    , getCurY()
                    , mThiCirRadius / 2
                    , mPaint);

            /*
            * 画扇形圆弧
            * 正在改变的属性：
            * mAroundArcDegrees（起始角度）
            * mAroundArcLength（扫过的范围）
            * */
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(20);
            canvas.drawArc(mRectF
                    , mAroundArcDegrees
                    , mAroundArcLength
                    , false
                    , mPaint);

            /*
            * 画最后一个和开头衔接的圆
            * 正在改变的属性：
            *
            *
            * */
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(mPreFirCirRadius);
            canvas.drawCircle(getCurX()
                    , getCurY()
                    , mPreFirCirRadius / 2
                    , mPaint);

        }

        if (isChangeEnd) {
            if (!isEndAnimStart) {
                mEndAnimator.start();
                isEndAnimStart = true;
            }
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Config.BLUE);
            canvas.drawCircle(getCurX(), getCurY(), mEndCirIRadius, mPaint);
            mPaint.setColor(Config.RED);
            canvas.drawCircle(getCurX(), getCurY(), mEndCirMRadius, mPaint);
            mPaint.setColor(Config.YELLOW);
            canvas.drawCircle(getCurX(), getCurY(), mEndCirORadius, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            return;
        }

    }



}
