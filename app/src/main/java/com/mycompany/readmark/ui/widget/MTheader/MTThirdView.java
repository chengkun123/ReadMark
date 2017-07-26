package com.mycompany.readmark.ui.widget.MTheader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

import com.mycompany.readmark.R;


/**
 * Created by Lenovo on 2017/6/5.
 */

public class MTThirdView extends View {
    private Bitmap endBitmap;


    public MTThirdView(Context context) {
        this(context, null);
    }

    public MTThirdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTThirdView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MTThirdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        endBitmap = Bitmap.createBitmap(
                BitmapFactory.decodeResource(getResources(), R.mipmap.pull_end_image_frame_05));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int resultWidth = getResultWidth(widthMeasureSpec);
        //和endBitmap宽高成比例
        int resultHeight = (int)(resultWidth * ((float)endBitmap.getHeight() / (float)endBitmap.getWidth() ));
        setMeasuredDimension(resultWidth, resultHeight);

    }

    /*
    * 支持at_most,计算出最终宽度
    * */
    private int getResultWidth(int widthMeasureSpec){
        int result = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (mode){
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                //取可用的和endBitmap宽度中的最小值
                result = Math.min(result, endBitmap.getWidth());
                break;
        }

        return result;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }



}