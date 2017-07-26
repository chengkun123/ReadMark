package com.mycompany.readmark.ui.widget.MTheader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.mycompany.readmark.R;


/**
 * Created by Lenovo on 2017/6/5.
 */

public class MTFirstView extends View {

    private Bitmap startBitmap;
    private Bitmap endBitmap;
    private Bitmap realBitmap;
    private float mProgress;
    private int maxWidth;
    private int maxHeight;

    public MTFirstView(Context context) {
        this(context, null);
    }

    public MTFirstView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTFirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MTFirstView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initBitmaps(context);
    }


    private void initBitmaps(Context context){
        startBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources()
                , R.mipmap.pull_image));
        endBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources()
                , R.mipmap.pull_end_image_frame_05));

    }



    /*
    * 根据下一状态图片宽高，设置本状态图片宽高最大值作为View的宽高。
    * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int resultWidth = getResultWidth(widthMeasureSpec);
        //让View的大小和第二阶段的Bitmap等比例
        setMeasuredDimension(resultWidth,
                (int)(resultWidth * ((float)endBitmap.getHeight() / (float)endBitmap.getWidth())));

    }

    /*
    * 支持at_most
    * */
    private int getResultWidth(int widthMeasureSpec){
        int result = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (mode){
            case MeasureSpec.EXACTLY:
                return result;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, endBitmap.getWidth());
                break;
        }

        return result;
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        maxWidth = getMeasuredWidth();
        maxHeight = getMeasuredHeight();



        //最终Bitmap和初始Bitmap成比例
        realBitmap = Bitmap.createScaledBitmap(startBitmap, maxWidth
                , (int)(maxWidth*((float)startBitmap.getHeight() / (float)startBitmap.getWidth())), true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(mProgress, mProgress, maxWidth/2, maxHeight/2);

        canvas.drawBitmap(realBitmap, 0, 0, null);
    }


    public void setProgress(float progress){
        mProgress = progress;
        postInvalidate();
    }
}
