package com.mycompany.readmark.ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.mycompany.readmark.R;

/**
 * Created by Lenovo.
 */

public class WaveLoadingView extends View {

    /*
    * 如果没有指定属性，使用下面默认值
    * */

    //默认振幅比
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.1f;
    //默认水位比
    private static final float DEFAULT_WAVE_HEIGHT_RATIO = 0.5f;
    //默认波长比
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    //默认偏移比
    private static final float DEFAULT_AWAY_LEFT_RATIO = 0.0f;

    private static final int DEFAULT_WAVE_COLOR = Color.parseColor("#E91E63");
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#212121");
    private static final float DEFAULT_TITLE_CENTER_SIZE = 64.0f;

    private static final int DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE.ordinal();

    //三种类型
    public enum ShapeType {
        CIRCLE,
        SQUARE,
    }

    /*
    * 支持wrap_content
    * */
    private int mMinumWidth;
    private int mMinumHeight;


    private Context mContext;
    private int mType;

    private Paint mBorderPaint;
    //private Paint mBgPaint;
    private Paint mWavePaint;
    private Matrix mWaveShaderMatrix;
    private int mWaveColor;
    private int mBorderColor;
    private BitmapShader mWaveShader;

    private Paint mTitlePaint;
    private String mTitletext;

    private float mWaveLengthRatio;
    private float mWaveAmplitudeRatio;
    //用于形成标准的初始Shader
    private float mDefaultWaveHeight;
    private float mWaveHeightRatio = 0.5f;

    //动画相关
    private ObjectAnimator mWavingAnim;
    private AnimatorSet mWavingAnimSet;
    //形成波动效果的关键
    private float mAwayLeftRatio = 0.0f;


    public WaveLoadingView(Context context) {
        this(context, null);
    }

    public WaveLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WaveLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        mContext = context;

        mMinumWidth = dp2px(48);
        mMinumHeight = dp2px(48);

        TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.WaveLoadingView, defStyleAttr, 0);
        mType = attributes.getInteger(R.styleable.WaveLoadingView_wlv_shapeType, DEFAULT_WAVE_SHAPE);
        mWaveColor = attributes.getInteger(R.styleable.WaveLoadingView_wlv_waveColor, DEFAULT_WAVE_COLOR);
        //最后范围1~2个宽度
        float lengthRatio = attributes.getFloat(R.styleable.WaveLoadingView_wlv_waveLengthRation, DEFAULT_WAVE_LENGTH_RATIO);
        mWaveLengthRatio = 1.0f + 1.0f * (lengthRatio > 1.0f ? 1.0f : lengthRatio);
        //最后范围0.1~0.4个高度
        float ampRatio = attributes.getFloat(R.styleable.WaveLoadingView_wlv_waveAmplitudeRatio, DEFAULT_AMPLITUDE_RATIO);
        mWaveAmplitudeRatio = 0.5f + 0.5f * (ampRatio > 1.0f ? 1.0f : ampRatio);


        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(dp2px(2));

        //mBgPaint = new Paint();

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWaveShaderMatrix = new Matrix();

        mTitlePaint = new Paint();
        mTitlePaint.setColor(DEFAULT_TITLE_COLOR);
        mTitlePaint.setStyle(Paint.Style.FILL);
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setTextSize(DEFAULT_TITLE_CENTER_SIZE);

        initBasicTravelAnimation();
        attributes.recycle();
    }


    /**
     * wave平移动画，一开始就启动
     */
    private void initBasicTravelAnimation(){
        mWavingAnim = ObjectAnimator.ofFloat(this, "awayLeftRatio", 0f, 1f);
        mWavingAnim.setRepeatCount(ValueAnimator.INFINITE);
        mWavingAnim.setDuration(1000);
        mWavingAnim.setInterpolator(new LinearInterpolator());
        mWavingAnim.start();
        /*mWavingAnimSet = new AnimatorSet();
        mWavingAnimSet.play(mWavingAnim);*/

    }

    /**
     * 为awayLeftRatio提供set方法，针对属性动画
     * @param awayLeftRatio
     */
    private void setAwayLeftRatio(float awayLeftRatio){
        if(awayLeftRatio != mAwayLeftRatio){
            mAwayLeftRatio = awayLeftRatio;
            invalidate();
        }
    }

    /**
     * 为awayLeftRatio提供get方法，针对属性动画
     * @return
     */
    private float getAwayLeftRatio(){
        return mAwayLeftRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        switch (mType){
            case 0:
                setMeasuredDimension(Math.min(width, height), Math.min(width, height));
                break;
            case 1:
                setMeasuredDimension(Math.min(width, height), Math.min(width, height));
                break;
            default:
                setMeasuredDimension(Math.min(width, height), Math.min(width, height));
                break;
        }
    }

    private int measureWidth(int measureSpec){
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        int result = size;
        switch (mode){
            case MeasureSpec.EXACTLY:
                return result;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, mMinumWidth);
        }
        return result;
    }

    private int measureHeight(int measureSpec){
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        int result = size;
        switch (mode){
            case MeasureSpec.EXACTLY:
                return result;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, mMinumHeight);
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        produceShader();

    }

    /**
     * 产生shader
     *
     */
    private void produceShader(){

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);

        /*
        * 以1个宽度为波长，0.1个高度为振幅，水平面为0.5个高度画水波。
        * */
        double angularFrequency = 2 * Math.PI / (width * 1.0f);
        float amplitude = 0.1f * height;
        mDefaultWaveHeight = height * 0.5f;

        //存放竖线的起点
        int[] startY = new int[width];

        //画后面的水波
        linePaint.setColor(setBackWaveColor(mWaveColor, 0.3f));
        for(int i=0; i<width; i++){
            startY[i] = (int) (amplitude * Math.sin(angularFrequency * i) + mDefaultWaveHeight);
            canvas.drawLine(i, startY[i], i, height, linePaint);
        }

        //画前面的水波，偏移1/4个波长
        linePaint.setColor(mWaveColor);
        for(int i=0; i<width; i++){
            canvas.drawLine(i, startY[(i + width/4) % width], i, height, linePaint);
        }


        //以这个Bitmap为基准形成着色器
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mWavePaint.setShader(mWaveShader);
    }



    /**
     *
     * 为后面的wave设置透明度
     * @param waveColor
     * @param factor
     * @return
     */
    private int setBackWaveColor(int waveColor, float factor){
        int alpha = Math.round(Color.alpha(waveColor) * factor);
        int red = Color.red(waveColor);
        int green = Color.green(waveColor);
        int blue = Color.blue(waveColor);
        return Color.argb(alpha, red, green, blue);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //这里的矩阵是相对于Paint将要绘制的区域的（还是View?）
        //一定要reset或setscale
        mWaveShaderMatrix.reset();
        //根据用户的设置的波长以及振幅比例缩放
        mWaveShaderMatrix.postScale(mWaveLengthRatio, mWaveAmplitudeRatio, 0, mDefaultWaveHeight);
        //平移Shader产生动画效果
        mWaveShaderMatrix.postTranslate(mAwayLeftRatio * getMeasuredWidth() * mWaveLengthRatio
                , (0.5f - mWaveHeightRatio) * getHeight());
        //确定最终效果
        mWaveShader.setLocalMatrix(mWaveShaderMatrix);

        float borderWidth = mBorderPaint.getStrokeWidth();

        /*CIRCLE,SQUARE,RECTANGLE*/
        switch (mType){
            //圆
            case 0:
                //绘制
                int radius = Math.min(getWidth(), getHeight()) / 2;

                if(borderWidth > 0){
                    mBorderPaint.setColor(mWaveColor);
                    canvas.drawCircle(radius, radius, (2 * radius - borderWidth) / 2f - 1f, mBorderPaint);
                }
                canvas.drawCircle(radius, radius, radius, mWavePaint);

                break;
            //正方形
            case 1:
                if(borderWidth > 0){
                    canvas.drawRect(
                            borderWidth / 2f,
                            borderWidth / 2f,
                            Math.min(getWidth(), getHeight()) - borderWidth / 2f - 0.5f,
                            Math.min(getWidth(), getHeight()) - borderWidth / 2f - 0.5f,
                            mBorderPaint);
                }
                canvas.drawRect(borderWidth
                        , borderWidth
                        , Math.min(getWidth(), getHeight()) - borderWidth
                        , Math.min(getWidth(), getHeight()) - borderWidth
                        , mWavePaint);
                break;
            //长方形
            //不太好看
            /*case 2:
                if(borderWidth > 0){
                    canvas.drawRect(borderWidth / 2f,
                            borderWidth / 2f,
                            getWidth() - borderWidth / 2f - 0.5f,
                            getHeight() - borderWidth / 2f - 0.5f,
                            mBorderPaint);
                }
                canvas.drawRect(borderWidth
                        , borderWidth
                        , getWidth() - borderWidth
                        , getHeight() - borderWidth
                        , mWavePaint);
                break;*/
        }

        float textWidth = mTitlePaint.measureText(mTitletext);
        canvas.drawText(mTitletext
                , (getWidth() - textWidth) / 2
                , getHeight() / 2 - (mTitlePaint.descent() + mTitlePaint.ascent()) / 2
                , mTitlePaint);


    }


    /**
     * 设置进度后启动一个动画
     * @param progress
     */
    public void setProgress(float progress){
        if(progress >= 1){
            progress = 1;
        }else if(progress <= 0){
            progress = 0;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "waveHeightRatio", mWaveHeightRatio, progress);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        mWaveHeightRatio = progress;
        animator.start();
    }

    private void setWaveHeightRatio(float progress){
        if(mWaveHeightRatio != progress){
            mWaveHeightRatio = progress;
            invalidate();
        }
    }

    private float getWaveHeightRatio(){
        return mWaveHeightRatio;
    }


    /**
     * 设置水波颜色
     * @param color
     */
    public void setWaveColor(int color){
        mWaveColor = color;
        //需要重新修改Shader
        produceShader();
        invalidate();
    }

    public int getWaveColor(){
        return mWaveColor;
    }

    //0~1
    public void setWaveLengthRatio(float waveLengthRatio) {
        mWaveLengthRatio = waveLengthRatio + 1;
        //initBasicTravelAnimation();
        invalidate();
    }

    public float getDefaultWaveLengthRatio(){
        return mWaveLengthRatio;
    }

    public void setWaveAmplitudeRatio(float waveAmplitudeRatio) {
        mWaveAmplitudeRatio = 0.5f + 0.5f * waveAmplitudeRatio;
        invalidate();
    }

    public float getWaveAmplitudeRatio() {
        return mWaveAmplitudeRatio;
    }


    public void setShapeType(ShapeType shapeType){
        mType = shapeType.ordinal();
        invalidate();
        /*if(shapeType == ShapeType.RECTANGLE){
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = originMeasuredWidth;
            params.height = originmeasuredHeight;
            setLayoutParams(params);
        }else{
            invalidate();
        }*/
    }

    public int getShapeType(){
        return mType;
    }

    public String getTitletext() {
        return mTitletext;
    }

    public void setTitletext(String titletext) {
        mTitletext = titletext;
        invalidate();
    }

    /**
     * dp和px转换工具
     *
     * @param spValue The real size of text
     * @return int - A transplanted sp
     */
    private int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}