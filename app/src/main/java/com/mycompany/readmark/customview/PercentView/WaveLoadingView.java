package com.mycompany.readmark.customview.PercentView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


public class WaveLoadingView extends View {

    private Paint mWavePaint;

    private PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    private Paint mCirclePaint;
    private Paint mTextPaint;
    //自己的画布
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private int mWidth;
    private int mHeight;
    private int x;
    private int y;
    private Path mPath;

    private boolean isLeft;
    private float mPercent = 0;
    private boolean isFirstLoad = true;

    public WaveLoadingView(Context context){
        this(context, null);
    }

    public WaveLoadingView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public WaveLoadingView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mPath = new Path();
        mWavePaint = new Paint();
        mWavePaint.setColor(Color.parseColor("#8800ff66"));
        mWavePaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.parseColor("#88dddddd"));
        mCirclePaint.setAntiAlias(true);



        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);


    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        /*
        * 对wrap_content的支持
        * */
        int widthInWrapContent = dp2px(100);
        int heightInWrapContent = dp2px(100);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY){
            mWidth = widthSize;
        }else if(widthMode == MeasureSpec.AT_MOST){
            mWidth = widthInWrapContent > widthSize ? widthSize : widthInWrapContent;
        }else{
            mWidth = widthInWrapContent;
        }

        if(heightMode == MeasureSpec.EXACTLY){
            mHeight = heightSize;
        }else if (heightMode == MeasureSpec.AT_MOST){
            mHeight = heightInWrapContent > heightSize ? heightSize : heightInWrapContent;
        }else{
            mHeight = heightInWrapContent;
        }

        mWidth = Math.min(mWidth, mHeight);

        y = mWidth;

        setMeasuredDimension(mWidth, mWidth);


        mBitmap = Bitmap.createBitmap(mWidth, mWidth, Bitmap.Config.ARGB_8888);
        //生成一个Canvas用于塑造Bitmap
        //将Bitmap放在自己的画布上，实际上mCanvas.draw的时候改变的是这个Bitmap对象。
        mCanvas = new Canvas(mBitmap);

    }

    protected void onDraw(Canvas canvas){

        //当前方向改为向左
        if(x > mWidth / 4){
            isLeft = true;
        }else if(x < 0){//当前方向改为向右
            isLeft = false;
        }
        //向左时，x减小
        if (isLeft) {
            x = x - mWidth/300;
        } else {//向右时，x增大
            x = x + mWidth/300;
        }

        y = (int) ((1 - mPercent) * mWidth);


        //画了一个以方块为底，三阶贝塞尔曲线为顶的图形
        //Path在每次onDraw的时候重置
        mPath.reset();
        mPath.moveTo(0, y);
        mPath.cubicTo(mWidth/4 + 2 * x, y + mWidth/12, mWidth/4 + 2 * x, y - mWidth/12, mWidth, y);
        mPath.lineTo(mWidth, mWidth);
        mPath.lineTo(0, mWidth);
        mPath.close();
        //绘制之前清空原来的Bitmap
        mBitmap.eraseColor(Color.parseColor("#00000000"));

        //先画圆dst
        mCanvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mCirclePaint);
        //模式设置为src_in
        mWavePaint.setXfermode(mMode);
        //后画水波矩形src
        mCanvas.drawPath(mPath, mWavePaint);

        //再将Bitmap画到系统canvas上
        canvas.drawBitmap(mBitmap, 0, 0, null);

        //画百分比
        String str = ((int)(mPercent*100))+"";
        mTextPaint.setTextSize(mWidth / 8);
        float txtLength = mTextPaint.measureText(str);
        canvas.drawText(str, mWidth / 2 - txtLength / 2, mWidth / 2 + mWidth / 40, mTextPaint);


        mTextPaint.setTextSize(mWidth/15);
        canvas.drawText("%", mWidth / 2 + mWidth/12, mWidth / 2 - mWidth/30, mTextPaint);

        //进行重绘
        super.onDraw(canvas);
        //10ms重绘一次，形成波动效果。
        postInvalidateDelayed(10);
    }

    public void setPercent(float percent){
        mPercent = percent;
    }
    public float getPercent(){
        return mPercent;
    }

    public boolean getFirstLoading(){
        return isFirstLoad;
    }
    public void setFirstLoad(boolean b){
        isFirstLoad = b;
    }

    private int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , value, getResources().getDisplayMetrics());

    }

}