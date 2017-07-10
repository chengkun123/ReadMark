package com.mycompany.readmark.ui.widget.customloadinganimview.cakes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.mycompany.readmark.ui.widget.customloadinganimview.Config;


/**
 * Created by Lenovo.
 */

public class SecondCake extends Cake {

    private final int MOVE_MAX_LINTH = 120;
    //环绕线粗，应为AROUND_POINT_RADIUS*2;
    private final int LINE_STROKE_LENGTH = 8;
    //环绕行星(圆)半径
    private final int AROUND_POINT_RADIUS = 4;

    private boolean isMoveEnd;
    private Paint mPaint;
    //第二个黄色球
    private int mSecYellowCirRadius;
    private int mRedCirStrokeFactor;
    //红球外半径,比最大半径稍微小一点
    private int mRedCirCleRadius = 50;
    private int mFirYellowCirRadius;
    //黄球左偏移
    private int mLineLeftOffset;
    private int mLineRightOffset;
    private int mLineStrokeWidth = 120;


    //环绕角度
    private int mAroundLineDegrees;
    //环绕线内点
    private int mAroundLineInsideP = MAX_RADIUS_CIRCLE;
    //环绕线外点
    private int mAroundLineOutsideP = MAX_RADIUS_CIRCLE;

    //环绕偏离中心的坐标
    private int mAroundPointY = 0;


    private boolean mIsCirLineShow = true;
    //环绕行星可见性
    private boolean mIsAroundPointV = false;

    private ValueAnimator firAnimator;
    private ValueAnimator secAnimator;
    private ValueAnimator thirdAnimator;


    private ValueAnimator mEndAnimator;
    private int mEndCirIRadius;
    private int mEndCirMRadius;
    private int mEndCirORadius;

    public SecondCake(int curX, int curY) {
        super(curX, curY);
    }

    @Override
    protected void initConfig() {
        mEndMovingLength = -130;
        mPaint = new Paint();
        //mPaint.setStyle(Paint.Style.STROKE);
        //设置画一条直线后结尾处的形状
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //设置连接处的形状
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void initEndAnim() {
        mEndAnimator = ValueAnimator.ofFloat(0, 1, 2).setDuration(mEndDuration);
//        mEndAnimator.setRepeatCount(2);
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
    }



    protected void initAnim(){

        /*
        * 形成连接线等
        * */
        firAnimator = ValueAnimator.ofFloat(0, 1, 2);
        firAnimator.setDuration(600);
        firAnimator.setRepeatCount(0);
        firAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                if(factor < 1){
                    mFirYellowCirRadius = 20 + (int) (factor * (MAX_RADIUS_CIRCLE - 20));
                    mRedCirStrokeFactor = (int) (14 + factor * 20);
                }else{
                    mLineLeftOffset = (int) (MOVE_MAX_LINTH * (factor - 1));
                }
            }
        });
        firAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFirYellowCirRadius = 0;
                mRedCirCleRadius = 60;
                mRedCirStrokeFactor = 50;
                mSecYellowCirRadius = 0;
                secAnimator.start();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsCirLineShow = true;
            }
        });


        /*
        * 黄线消失、卫星线显示、卫星球显示等
        *
        * */

        secAnimator = ValueAnimator.ofFloat(0, 0.5f, 1, 1.25f, 1.5f, 1.75f, 2);
        secAnimator.setDuration(2400);
        secAnimator.setRepeatCount(0);
        secAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                if(factor <= 1.0f){
                    mLineRightOffset = (int) (MOVE_MAX_LINTH * factor);
                }else{
                    //不画连接线了
                    mIsCirLineShow = false;
                    mAroundLineDegrees = (int) (90 * (2 - factor));
                    if(factor < 1.5){
                        factor =  (float)(1.5-factor) * 2;
                        mAroundLineInsideP = MAX_RADIUS_CIRCLE - (int) (MAX_RADIUS_CIRCLE * factor);
                        //红球缩小至20时就停止缩小
                        if (mRedCirCleRadius > 20) {
                            mRedCirStrokeFactor = (int) (50 * factor);
                            mRedCirCleRadius = MAX_RADIUS_CIRCLE - mAroundLineInsideP;
                        }
                        //中心小黄球出现
                        mFirYellowCirRadius = (int) (10 * (1 - factor));

                    }
                    //底层黄球出现（环）
                    if (factor > 1.5) {
                        factor = (factor - 1.5f) * 2;
                        mIsAroundPointV = true;
                        mAroundPointY = (int) ((MAX_RADIUS_CIRCLE / 2) * factor);
                        mSecYellowCirRadius = (int) (30 * factor);
                    }
                }
            }
        });

        secAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                thirdAnimator.start();
            }
        });


        /*
        * 卫星线内聚等
        *
        * */
        thirdAnimator = ValueAnimator.ofFloat(0, 5f);
        thirdAnimator.setDuration(3000);
        thirdAnimator.setRepeatCount(0);
        thirdAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                float changeValue = 0;
                if (factor < 1.0f) {
                    //黄球放大
                    mSecYellowCirRadius = (int) (30 + 15 * (factor));
                } else if (factor < 2.0f) {
                    changeValue = 2 - factor;
                    //黄球缩小
                    mSecYellowCirRadius = (int) (45 * changeValue);
                    //点成线
                    mAroundLineInsideP = MAX_RADIUS_CIRCLE - (int) (MAX_RADIUS_CIRCLE / 3 * (1 - changeValue));
                } else if (factor < 2.25f) {
                    //停顿一下。
                } else if (factor < 3.0f) {
                    changeValue = (3.0f - factor) * 4 / 3;
                    //线内聚
                    mIsAroundPointV = false;
                    mAroundLineOutsideP = (int) (MAX_RADIUS_CIRCLE * changeValue);
                    mAroundLineInsideP = (int) (MAX_RADIUS_CIRCLE / 3 * 2 * changeValue);
                    //黄球缩小,红球放大
                    mFirYellowCirRadius = (int) (10 * changeValue);
                    mRedCirStrokeFactor = 30;//(int) (16 + 10 * (1 - (3.0f - factor) * 4 / 3));
                    mRedCirCleRadius = (int) (20 + 15 * (1 - changeValue));
                } else if (factor < 4.0f) {
                    //停顿一下
                } else if (factor < 5.0f) {
                    changeValue = factor - 4.0f;
                    //红球（环）内边外扩，内部黄球放大
                    mFirYellowCirRadius = (int) (20 * changeValue);
                    mRedCirStrokeFactor = (int) (30 - 16 * changeValue);
                    mRedCirCleRadius = (int) (35 + 8 * changeValue);
                }


            }
        });
        thirdAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAroundLineInsideP = MAX_RADIUS_CIRCLE;
                mAroundLineOutsideP = MAX_RADIUS_CIRCLE;
                mLineLeftOffset = 0;
                mLineRightOffset = 0;

                if(mListener != null){
                    mListener.onCakeChangeAnimatorEnd();
                }
            }
        });


    }

    public void startAnim(){
        firAnimator.start();
    }



    public void drawSelf(Canvas canvas){
        if(!isMoveEnd){
            //后面可能会改变Style，先重置
            if(mPaint.getStyle() != Paint.Style.STROKE){
                mPaint.setStyle(Paint.Style.STROKE);
            }

            /*
            * 实心黄圈
            * */
            mPaint.setColor(Config.YELLOW);
            mPaint.setStrokeWidth(mSecYellowCirRadius);
            canvas.drawCircle(getCurX()
                    , getCurY()
                    , mSecYellowCirRadius / 2
                    , mPaint);
            /*
            * 红色圈
            * 半径的意思是让stroke刚好置于内侧
            * */
            mPaint.setColor(Config.RED);
            mPaint.setStrokeWidth(mRedCirStrokeFactor);
            canvas.drawCircle(getCurX()
                    , getCurY()
                    , mRedCirCleRadius - mRedCirStrokeFactor / 2
                    , mPaint);

            /*
            * 实心黄圈
            * */
            mPaint.setColor(Config.YELLOW);
            mPaint.setStrokeWidth(mFirYellowCirRadius);
            canvas.drawCircle(getCurX()
                    , getCurY()
                    , mFirYellowCirRadius / 2
                    , mPaint);

            /*
            * 画连接线
            *
            * */
            if (mIsCirLineShow) {
                mPaint.setStrokeWidth(mLineStrokeWidth);
                canvas.drawLine(getCurX() + mLineRightOffset
                        , getCurY()
                        , getCurX() + mLineLeftOffset
                        , getCurY(), mPaint);
            }

            /*
            * 画卫星线
            *
            * */
            mPaint.setColor(Config.RED);
            mPaint.setStrokeWidth(LINE_STROKE_LENGTH);
            for(int i=0; i<8; i++){
                canvas.save();
                canvas.rotate(45 * i + mAroundLineDegrees, getCurX(), getCurY());
                canvas.drawLine(getCurX()
                        , getCurY() - mAroundLineOutsideP
                        , getCurX()
                        , getCurY() - mAroundLineInsideP
                        , mPaint);
                canvas.restore();
            }

            /*
            * 画卫星
            *
            * */
            if(mIsAroundPointV == true){
                mPaint.setStyle(Paint.Style.FILL);
                for (int i=0; i<8; i++){
                    mPaint.setAlpha(160);
                    canvas.save();
                    canvas.rotate(45 * i + mAroundLineDegrees
                            , getCurX()
                            , getCurY());
                    canvas.drawCircle(getCurX()
                            , getCurY() - MAX_RADIUS_CIRCLE / 2 - mAroundPointY
                            , AROUND_POINT_RADIUS
                            , mPaint);
                    canvas.restore();
                }
            }
        }

        if (isChangeEnd) {
            if (!isEndAnimStart) {
                mEndAnimator.start();
                isEndAnimStart = true;
            }
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Config.GREEN);
            canvas.drawCircle(getCurX(), getCurY(), mEndCirIRadius, mPaint);
            mPaint.setColor(Config.YELLOW);
            canvas.drawCircle(getCurX(), getCurY(), mEndCirMRadius, mPaint);
            mPaint.setColor(Config.RED);
            canvas.drawCircle(getCurX(), getCurY(), mEndCirORadius, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            return;
        }
    }
}
