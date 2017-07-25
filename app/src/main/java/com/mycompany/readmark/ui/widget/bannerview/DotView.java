package com.mycompany.readmark.ui.widget.bannerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Lenovo.
 */

public class DotView extends View {

    //用来绘制图形
    private Drawable mDrawable;

    public DotView(Context context) {
        this(context, null);
    }

    public DotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mDrawable != null){
            Bitmap bitmap = drawableToBitmap(mDrawable);
            Bitmap resultCircle = getCircleBitmap(bitmap);
            //Bitmap绘制到画布上
            canvas.drawBitmap(resultCircle, 0, 0, null);
        }

    }

    /**
     * 把bitmap变圆
     *
     * @param bitmap
     * @return
     */
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        //创建一个Bitmap，准备在上面作画
        Bitmap base = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(base);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        //防抖动
        paint.setDither(true);

        //先在base上画一个圆
        canvas.drawCircle(getMeasuredWidth() / 2
                , getMeasuredHeight() / 2
                , getMeasuredWidth() / 2, paint);
        //设置为SRC_IN
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //再在base上画上传进来的bitmap，注意坐标对应（这里从原点开始）
        canvas.drawBitmap(bitmap, 0, 0, paint);

        //回收
        bitmap.recycle();
        bitmap = null;
        return base;
    }


    /**
     * 把Drawble转换成Bitmap
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        //如果是BitmapDrawable，直接可获取Bitmap
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        }
        //如果是ColorDrawable，我们创建一个Bitmap并把颜色“画”上去

        //创建一个Bitmap准备在上面作画
        Bitmap base = Bitmap.createBitmap(getMeasuredWidth()
                , getMeasuredHeight()
                , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(base);

        //drawable要设置边界
        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        //把drawable画到base上
        drawable.draw(canvas);

        return base;
    }

    /**
     * 提供设置Drawable的接口
     * @param drawable
     */
    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        invalidate();
    }
}
