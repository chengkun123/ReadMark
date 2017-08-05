package com.mycompany.readmark.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
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
import com.mycompany.readmark.ui.adapter.TagAdapter;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo.
 */
public class FlowLayout extends ViewGroup implements View.OnLongClickListener{
    private float mWindowXDown;
    private float mWindowYDown;
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
    private SparseBooleanArray mCheckedTagArray = new SparseBooleanArray();
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

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //首先调用View的OnMeasure传入Spec，设置FlowLayout的测量宽高
        //由于AT_MOST模式和EXACTLY模式MeasuredWidth/Height会选取Spec中的值，
        // 所以下面还要支持FlowLayout的参数为wrap_content的情况
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


        /*
        * 先对子View进行测量，将measure传递下去
        *
        * */

        //计算最大行宽、总高度
        for(int i=0; i<count; i++){

            View child = getChildAt(i);
            //先把测量分发下去
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();
            //获取TextView的测量值
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //计算添加此child后的行宽，考虑子View的margin
            int tempWidth = lineWidth +  lp.leftMargin + childWidth + lp.rightMargin;

            //如果需要换行，注意要减去padding
            if (tempWidth > sizeWidth - paddingLeft - paddingRight){
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


        /*
        * 对自身宽高进行设定
        *
        * */

        // 如果是EXACTLY(对应match_parent和具体值)，就设置Spec中的值，
        // 如果是AT_MOST（对应wrap_content），就设置为根据子View布局计算的值width。
        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + paddingLeft + paddingRight,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + paddingTop + paddingBottom);
    }


    //onLayout中确定子View的位置
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

    //子View获取的LayoutParams
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

    class AdapterDataSetObserver extends DataSetObserver {
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
        /*
        * 这个方法做了三件事
        * removeAllViewsInLayout();
        * requestLayout();
        * invalidate(true);
        * */
        removeAllViews();
        for(int i=0; i<mAdapter.getCount(); i++){
            final int j = i;
            // 注意最后一个参数传入FlowLayout
            final View child = mAdapter.getView(j, null, this);
            //重置点击状态为false
            mCheckedTagArray.put(i, false);
            addView(child);
            child.setOnLongClickListener(this);
            //长按和点击事件
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnTagClickListener.onTagClick((String) mAdapter.getItem(j));
                }
            });
        }

    }

    /*
    * 用于初始化坐标
    * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mWindowXDown = ev.getRawX();
                mWindowYDown = ev.getRawY();
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
                //当开始Move的时候，如果是Drag模式事件交由FlowLayout处理
                if(mMode == MODE_DRAG){
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //getParent().requestDisallowInterceptTouchEvent(true);
                intercepted = false;
                break;
            default:
                break;
        }

        return intercepted;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if(mMode == MODE_DRAG){
                    float wx = event.getRawX() - mRelativeX;
                    float wy = event.getRawY() - mRelativeY;

                    /*
                    * event在FlowLayout中的坐标
                    * */
                    float xInparent = event.getX();
                    float yInparent = event.getY();

                    /*
                    * 查找hit的子View
                    * */
                    int dropPos = pointToPosition(xInparent, yInparent);

                    //移动Window
                    if(mWindowLayoutParams != null){

                        mWindowLayoutParams.x = (int)wx;
                        mWindowLayoutParams.y = (int)wy;
                        //改变Window的位置
                        mWindowManager.updateViewLayout(mDragView, mWindowLayoutParams);
                    }

                    if(dropPos == mOldPosition || dropPos == -1){
                        break;
                    }

                    //移动其他标签
                    startMove(dropPos);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mMode == MODE_DRAG){
                    //移除mDragView
                    if(mDragView != null){
                        mWindowManager.removeView(mDragView);
                        mDragView = null;
                        mWindowLayoutParams = null;
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
        /*return super.onTouchEvent(event);*/
        return true;
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
                //移动完毕后保留动画效果
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

    /*
    * 监听最后一个View的动画
    * */
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
        /*if(mMode == MODE_DRAG || pos == -1){
            return false;
        }*/
        mOldView = v;
        mOldView.setVisibility(INVISIBLE);
        //Log.e("oldView的状态", ""+ mOldView.getVisibility());
        mOldPosition = pos;
        mTempPosition = pos;

        //计算按下点和所属View边缘的偏差（在window中的偏差）
        Rect rect = new Rect();
        v.getGlobalVisibleRect(rect);

        mRelativeX = mWindowXDown - rect.left;
        mRelativeY = mWindowYDown - rect.top;

        if(Build.VERSION.SDK_INT >= 23){
            if(Settings.canDrawOverlays(getContext())){
                initWindow();
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
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
            ImageView cross = (ImageView) mDragView.findViewById(R.id.tag_close);
            cross.setVisibility(INVISIBLE);
            TextView tv = (TextView) mDragView.findViewById(R.id.tag_textview);
            tv.setText(((TextView)mOldView.findViewById(R.id.tag_textview)).getText());
        }
        if(mWindowLayoutParams == null){
            mWindowLayoutParams = new WindowManager.LayoutParams();

            mWindowLayoutParams.width = mOldView.getWidth();
            mWindowLayoutParams.height = mOldView.getHeight();

            //这里的坐标取值还是有点问题
            //暂时没有发现问题所在
            Rect rect = new Rect();

            mOldView.getGlobalVisibleRect(rect);
            mWindowLayoutParams.x = rect.left;
            mWindowLayoutParams.y = rect.top;

            /*int[] co = new int[2];
            mOldView.getLocationInWindow(co);
            mWindowLayoutParams.x = co[0];
            mWindowLayoutParams.y = co[1];*/

            /*mWindowLayoutParams.x = mOldView.getLeft() + this.getLeft();
            mWindowLayoutParams.y = mOldView.getTop() + this.getTop();*/

            //系统Window无需Activity的Context
            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mWindowLayoutParams.format = PixelFormat.RGBA_8888;
            mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

            //window外的touch事件交给下层window处理，window内的自身处理。
            //本身不聚焦，将touch事件交给下层window处理。
            mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        }
        //该window中的View开始绘制，并通知wms保存window状态
        mWindowManager.addView(mDragView, mWindowLayoutParams);
        mMode = MODE_DRAG;

    }


}
