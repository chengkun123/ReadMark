package com.mycompany.readmark.search;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mycompany.readmark.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/1/11.
 */
public class FlowLayout extends ViewGroup implements View.OnLongClickListener{
    private float mWindowX;
    private float mWindowY;
    private float mXInParent;
    private float mYInParent;
    private int mFirstTouchPosition = -1;
    private int mMode = MODE_NORMAL;
    private final static int MODE_DRAG = 1;
    private final static int MODE_NORMAL = 2;
    private View mOldView;
    private int mOldPosition;
    private View mDragView;
    private int mTempPosition;
    private float mRelativeX;
    private float mRelativeY;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private OnTagMovedListener mOnTagMovedListener;


    private List<List<View>> mViews = new ArrayList<>();
    private List<Integer> mLineHeight = new ArrayList<>();
    private AdapterDataSetObserver mDataSetObserver;
    private TagAdapter<String> mAdapter;
    private SparseBooleanArray mCheckedTagArray = new SparseBooleanArray();;
    private OnTagLongClickListener mOnTagLongClickListener;
    private OnTagClickListener mOnTagClickListener;

    //由于拖拽过后涉及数据的改动，交由Adapter去做
    public interface OnTagMovedListener{
        void onTagMoved(int fromPos, int toPos, boolean isMoving);
    }

    public interface OnTagLongClickListener {
        void onTagLongClick(boolean isDeleteShowed);
    }

    public interface OnTagClickListener {
        void onTagClick(String input);
    }

    public void setOnTagLongClickListener(OnTagLongClickListener onTagLongClickListener){
        mOnTagLongClickListener = onTagLongClickListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener){
        mOnTagClickListener = onTagClickListener;
    }

    public FlowLayout(Context context) {
        this(context, null);
    }


    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        //这个WindowManager是在Activity中产生的
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //首先调用View的OnMeasure传入Spec，设置FlowLayout的测量宽高
        //由于AT_MOST模式和EXACTLY模式MeasuredWidth/Height会选取Spec中的值，所以下面还要支持FlowLayou的参数为wrap_content的情况
        //这种情况下MeasuredWidth并不一定会选取Spec中的值（即父View提供的可用的最大值）
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //首先获取可用的最大值
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //最终宽高
        int width = 0;
        int height = 0;

        //行宽高
        int lineWidth = 0;
        int lineHeight = 0;

        int count = getChildCount();

        //为支持FlowLayout的padding属性，在最后返回FlowLayout的测量值时，要加上Padding值
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        //计算最大行宽、总高度
        for(int i=0; i<count; i++){

            View child = getChildAt(i);
            //由于自View是TextView（TextView重写了onMeasure支持自己的wrap_content），测量后可以得到正确的测量值
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();
            //获取TextView的测量值
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //计算添加此child后的行宽。
            int tempWidth = lineWidth +  lp.leftMargin + childWidth + lp.rightMargin;

            //如果需要换行，注意要减去padding
            if (tempWidth > sizeWidth - paddingLeft -paddingRight){
                //更新最终宽高
                width = Math.max(lineWidth, width);
                height += lineHeight;

                //更新行宽高
                lineWidth = childWidth;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;

            }else{//如果不需要换行
                //更新行宽高
                lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
                lineWidth = tempWidth;
                //更新最终宽
                width = Math.max(lineWidth, width);
            }
        } // end for

        //对当前行处理，更新最终宽高
        height += lineHeight;
        width = Math.max(width, lineWidth);

        //重新设置FlowLayout的MeasuredWidth，
        // 如果是EXACTLY(对应match_parent和具体值)，就设置Spec中的值，
        // 如果是AT_MOST（对应wrap_content），就设置为根据子View布局计算的值width。
        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + paddingLeft + paddingRight,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + paddingTop + paddingBottom);
    }


    //layout方法确定View的位置，onLayout方法确定子View的位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mViews.clear();
        mLineHeight.clear();

        int count = getChildCount();
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        List<View> row = new ArrayList<>();

        //支持父控件的padding属性
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        //int paddingBottom = getPaddingBottom();

        /*
        先确定每行的View
        *
        * */
        for(int i=0; i<count; i++){
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int tempWidth = lineWidth + lp.leftMargin + childWidth + lp.rightMargin;

            //如果需要换行，注意要减去padding
            if(tempWidth > width - paddingLeft - paddingRight){
                mLineHeight.add(lineHeight);
                mViews.add(row);
                //把这个View放到下一行
                //不能使用clear()
                row = new ArrayList<>();
                row.add(child);
                lineWidth = childWidth;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
            }else{//如果不需要换行
                row.add(child);
                lineWidth = tempWidth;
                lineHeight = Math.max(lineHeight, childHeight+lp.topMargin+lp.bottomMargin);
            }
        }
        //将最后一行相关信息添加
        mViews.add(row);
        mLineHeight.add(lineHeight);


        /*
        * 开始布局子View
        * */
        int left = paddingLeft;
        int top = paddingTop;
        for(int i=0; i<mViews.size(); i++){

            for(int j=0; j<mViews.get(i).size(); j++){
                View child = mViews.get(i).get(j);
                //如果不可见，不设置它的位置
                if(child.getVisibility() == View.GONE){
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc+ child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

                left = rc + lp.rightMargin;
            }
            left = paddingTop;
            top += mLineHeight.get(i);
        }
    }

    //与当前ViewGroup对应的LayoutParams为MarginLayoutParams
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }


    //为FlowLayout设置新Adapter
    public void setAdapter(TagAdapter<String> adapter){
        //注销监听者
        if(mAdapter != null && mDataSetObserver != null){
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        removeAllViews();
        mAdapter = adapter;
        mOnTagMovedListener = (OnTagMovedListener)mAdapter;
        //注册监听者
        if(mAdapter != null){
            //在FlowLayout内部创建内部类实现DataSetObserver
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public ListAdapter getAdapter(){
        return mAdapter;
    }
    /*
    * 此内部类作为一个Adapter的监听者，在Adapter调用notifyDataChanged时
    完成View重置的功能
    * */

    class AdapterDataSetObserver extends DataSetObserver{
        @Override
        public void onChanged() {
            super.onChanged();
            reloadViews();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

    //重置所有View
    private void reloadViews(){
        removeAllViews();
        for(int i=0; i<mAdapter.getCount(); i++){
            final int j = i;
            // 注意最后一个参数传入FlowLayout
            final View child = mAdapter.getView(j, null, this);
            //重置点击状态为false
            mCheckedTagArray.put(i, false);
            child.setOnLongClickListener(this);
            addView(child);
            //长按和点击事件

            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnTagClickListener.onTagClick((String) mAdapter.getItem(j));
                }
            });
        }// end for

    }

    /*
    * 用于初始化坐标
    * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mWindowX = ev.getRawX();
                mWindowY = ev.getRawY();
                mXInParent = ev.getX();
                mYInParent = ev.getY();
                mFirstTouchPosition = pointToPosition(mXInParent, mYInParent);
                Log.e("dispatch测试",""+mFirstTouchPosition);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;

        }

        return super.dispatchTouchEvent(ev);
    }

    /*
    * 如果有移动的动作，那么由FlowLayout接管
    * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //当开始Move的时候，事件交由FlowLayout处理
                intercepted = true;
                break;
            case MotionEvent.ACTION_UP:
                //getParent().requestDisallowInterceptTouchEvent(true);
                intercepted = false;
                break;
            default:
                break;
        }
        //默认不拦截,也就是只有Down的时候不拦截，move和up都会被拦截
        return intercepted;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //先处理一下event
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.e("FlowLayout", ""+"DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                //这里处理移动
                if(mMode == MODE_DRAG){
                    float wx = event.getRawX() - mRelativeX;
                    float wy = event.getRawY() - mRelativeY;
                    //移动Window
                    if(mWindowLayoutParams != null){
                        mWindowLayoutParams.x = (int)wx;
                        mWindowLayoutParams.y = (int)wy;
                        mWindowManager.updateViewLayout(mDragView, mWindowLayoutParams);
                    }

                    float xInparent = event.getX();
                    float yInparent = event.getY();

                    int dropPos = pointToPosition(xInparent, yInparent);

                    if(dropPos == mOldPosition || dropPos == -1){
                        break;
                    }
                    startMove(dropPos);
                }
                break;
            case MotionEvent.ACTION_UP:

                if(mMode == MODE_DRAG){
                    //需要进行WindowManager的释放
                    if(mDragView != null){
                        mWindowManager.removeView(mDragView);
                        mDragView = null;
                        mWindowLayoutParams = null;
                        /*Log.e("dragview为空", ""+(mDragView == null));
                        Log.e("layoutparams为空", ""+(mWindowLayoutParams == null));*/
                    }
                    if(mOldPosition == mTempPosition || mTempPosition == -1){
                        getChildAt(mTempPosition).setVisibility(VISIBLE);
                    }else{
                        mOnTagMovedListener.onTagMoved(mOldPosition, mTempPosition, false);
                    }
                    mMode = MODE_NORMAL;
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    /*
    * 标签拖动
    * */
    private void startMove(int dropPosition){
        TranslateAnimation animation;
        //后移
        if(dropPosition < mTempPosition){
            for(int i=dropPosition; i<mTempPosition; i++){
                View preView = getChildAt(i);
                View nextView = getChildAt(i+1);
                //移动效果理想
                float xTranslation = (nextView.getLeft() - preView.getLeft()) * 1f / preView.getWidth();
                float yTranslation = (nextView.getTop() - preView.getTop()) * 1f / preView.getHeight();

                animation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF
                        , 0
                        , Animation.RELATIVE_TO_SELF
                        , xTranslation
                        , Animation.RELATIVE_TO_SELF
                        , 0
                        , Animation.RELATIVE_TO_SELF
                        , yTranslation);

                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(100);
                animation.setFillAfter(true);

                if(i == mTempPosition - 1){
                    animation.setAnimationListener(animationListener);
                }
                preView.startAnimation(animation);
            }
        }else{
            for(int i=mTempPosition; i<dropPosition; i++){
                View preView = getChildAt(i);
                View nextView = getChildAt(i+1);
                float xTranslation = (preView.getLeft() - nextView.getLeft()) * 1f / nextView.getWidth();
                float yTranslation = (preView.getTop() - nextView.getTop()) * 1f / nextView.getHeight();
                animation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF
                        , 0
                        , Animation.RELATIVE_TO_SELF
                        , xTranslation
                        , Animation.RELATIVE_TO_SELF
                        , 0
                        , Animation.RELATIVE_TO_SELF
                        , yTranslation);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(100);
                animation.setFillAfter(true);
                if(i == dropPosition - 1){
                    animation.setAnimationListener(animationListener);
                }
                nextView.startAnimation(animation);
            }
        }

        mTempPosition = dropPosition;
    }

    //内部类实现接口
    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mOnTagMovedListener.onTagMoved(mOldPosition, mTempPosition, true);
            mOldPosition = mTempPosition;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /*
            * 根据点的坐标判断是第几个子View
            * */
    private int pointToPosition(float x, float y){
        Rect area = new Rect();

        final int count = getChildCount();
        for(int i=0; i<count; i++){
            final View child = getChildAt(i);
            if(child.getVisibility() == View.VISIBLE){
                child.getHitRect(area);
                if(area.contains((int)x, (int)y)){
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean onLongClick(View v) {
        int position = pointToPosition(mXInParent, mYInParent);
        return afterLongClick(v, mFirstTouchPosition);
    }

    /*
        * 长按Tag后开始处理拖拽
        * */
    private boolean afterLongClick(View v, int pos){
        if(mMode == MODE_DRAG || pos == -1){
            return false;
        }
        mOldView = v;
        mOldView.setVisibility(INVISIBLE);
        Log.e("oldView的状态", ""+ mOldView.getVisibility());
        mOldPosition = pos;
        mTempPosition = pos;
        mRelativeX = mWindowX - v.getLeft() - this.getLeft();
        mRelativeY = mWindowY - v.getTop() - this.getTop();

        if(Build.VERSION.SDK_INT >= 23){
            if(Settings.canDrawOverlays(getContext())){
                initWindow();
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                getContext().startActivity(intent);
            }
        }else{
            initWindow();
        }
        return true;
    }

    /*
    * 初始化Window
    * */
    private void initWindow(){
        if(mDragView == null){
            mDragView = View.inflate(getContext(), R.layout.textview_tag, null);
            //mDragView.setBackgroundColor(Color.LTGRAY);
            ImageView cross = (ImageView) mDragView.findViewById(R.id.tag_close);
            cross.setVisibility(INVISIBLE);
            TextView tv = (TextView) mDragView.findViewById(R.id.tag_textview);
            tv.setText(((TextView)mOldView.findViewById(R.id.tag_textview)).getText());
            /*mDragView.setClickable(false);
            mDragView.setLongClickable(false);*/
        }
        if(mWindowLayoutParams == null){
            mWindowLayoutParams = new WindowManager.LayoutParams();
            //根据原TextView形成新的Window
            mWindowLayoutParams.width = mOldView.getWidth();
            mWindowLayoutParams.height = mOldView.getHeight();
            //这里并没有计算标题栏？
            mWindowLayoutParams.x = mOldView.getLeft() + this.getLeft();
            mWindowLayoutParams.y = mOldView.getTop() + this.getTop();

            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mWindowLayoutParams.format = PixelFormat.RGBA_8888;
            mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            //mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            //mOldView.setVisibility(INVISIBLE);
        }

        mWindowManager.addView(mDragView, mWindowLayoutParams);
        mMode = MODE_DRAG;
        /*Log.e("dragview已创建",""+mDragView);
        Log.e("测试",""+mMode);
        Log.e("dragview 和 oldview相等","" + (mOldView == mDragView));*/

    }


}
