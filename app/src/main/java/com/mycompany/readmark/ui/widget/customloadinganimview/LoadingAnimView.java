package com.mycompany.readmark.ui.widget.customloadinganimview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Lenovo.
 */

public class LoadingAnimView extends View {
    public final static int WIDTH_DEFAULT = 850;
    public final static int HEIGHT_DEFAULT = 300;

    private int mWidth;
    private int mHeight;

    private int mCenterX;
    private int mCenterY;

    private CakeAndLetterManager mManager;
    private OnViewAnimEndListener mListener;

    private boolean isRepeatOn = false;
    private ValueAnimator mRepeatAnim;

    public LoadingAnimView(Context context) {
        this(context, null);
    }

    public LoadingAnimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LoadingAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(widthMeasureSpec, WIDTH_DEFAULT);
        int height = measureDimension(heightMeasureSpec, HEIGHT_DEFAULT);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int measureSpec, int defaultSize){
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        int result = size;
        switch (mode){
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, defaultSize);
                break;
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initTheWorld();
    }

    private void initTheWorld(){
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mCenterX = (int) (getTranslationX() + mWidth / 2);
        mCenterY = (int) (getTranslationY() + mHeight / 2);

        mManager = new CakeAndLetterManager(this, mCenterX, mCenterY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isRepeatOn){
            initRepeatAnim();
        }
        mManager.drawTheWorld(canvas);
    }

    private void initRepeatAnim(){
        //16ms重绘一次
        mRepeatAnim = ValueAnimator.ofInt(0, 1);
        mRepeatAnim.setRepeatCount(ValueAnimator.INFINITE);
        mRepeatAnim.setDuration(16);
        mRepeatAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                invalidate();
            }
        });
        mRepeatAnim.start();
        isRepeatOn = true;
    }


    public void onAllAnimEnd() {
        mListener.onViewAnimEnd();
    }

    public interface OnViewAnimEndListener{
        void onViewAnimEnd();
    }

    public void setOnViewAnimEndListener(OnViewAnimEndListener listener){
        mListener = listener;
    }

}
