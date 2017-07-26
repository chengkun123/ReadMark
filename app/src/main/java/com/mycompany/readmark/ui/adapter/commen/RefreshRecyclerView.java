package com.mycompany.readmark.ui.adapter.commen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Lenovo.
 */

public class RefreshRecyclerView extends WrapRecyclerView {

    private static final String TAG = "RefreshRecyclerView";
    // 下拉刷新的辅助类
    private RefreshViewCreator mRefreshCreator;
    // 下拉刷新头部的高度
    private int mRefreshViewHeight = 0;
    // 下拉刷新的头部View
    private View mRefreshView;
    // 手指按下的Y位置
    private int mFingerDownY;
    // 手指拖拽的阻力指数
    protected float mDragIndex = 0.35f;
    // 当前是否正在拖动
    private boolean mCurrentDrag = false;

    // 默认状态
    public int REFRESH_STATUS_NORMAL = 0x0011;
    // 继续下拉以刷新状态
    public int REFRESH_STATUS_PULL_DOWN_REFRESH = 0x0022;
    // 松开刷新状态
    public int REFRESH_STATUS_LOOSEN_REFRESHING = 0x0033;
    // 正在刷新状态
    public int REFRESH_STATUS_REFRESHING = 0x0044;
    // 当前的状态
    private int mCurrentRefreshStatus = REFRESH_STATUS_NORMAL;



    // 处理刷新回调监听
    private OnRefreshListener mListener;
    private boolean isAutoScroll;
    private boolean isAbleToPull;
    private boolean isTmpFirstDown = true;

    private boolean isRollingToRefreshing;
    private boolean isRollingToFInished;


    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mListener = listener;
    }



    public interface OnRefreshListener {
        void onRefresh();
    }

    public RefreshRecyclerView(Context context) {
        super(context);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 显式添加RefreshView
     * @param refreshCreator
     */
    public void addRefreshViewCreator(RefreshViewCreator refreshCreator){
        mRefreshCreator = refreshCreator;
        addRefreshView();
    }


    /**
     * 更新了Adapter也要重新attach refreshHeader
     * @param adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        addRefreshView();
    }

    /**
     *
     * 添加同于刷新的View
     *
     */
    private void addRefreshView() {
        //header，refreshHeader，footer都要交给Adapter记录
        Adapter adapter = getAdapter();
        if(adapter != null && mRefreshCreator != null){
            View refreshView = mRefreshCreator.getRefreshView(getContext(), this);
            if(refreshView != null){
                //
                //Log.e(TAG, "的确添加了");
                addHeaderView(refreshView);
                mRefreshView = refreshView;
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            Log.e(TAG, "onLayout");
            //如果有RefreshView，则将它移出屏幕
            if(mRefreshView != null && mRefreshViewHeight <= 0){
                mRefreshViewHeight = mRefreshView.getMeasuredHeight();
                if(mRefreshViewHeight > 0){
                    //Log.e(TAG, "的确布局了");
                    setRefreshViewMarginTop( - mRefreshViewHeight + 1);
                }
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //记录按下坐标最好的地方
                mFingerDownY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //进行回弹
                if(mCurrentDrag){

                    restoreRefreshView();
                }
                isTmpFirstDown = true;
                break;
        }

        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //正在刷新或者正在回滚，不作为
        if(mCurrentRefreshStatus == REFRESH_STATUS_REFRESHING || isAutoScroll){
            //Log.e(TAG, "在刷新状态进入");
            return true;
        }else if(!canScrollUp()){/*!canScrollUp()*/
            //直到能滑动的时候才记录按下点
            if(isTmpFirstDown){
                mFingerDownY = (int) e.getRawY();
                isTmpFirstDown = false;
            }
            switch (e.getAction()){
                /*case MotionEvent.ACTION_DOWN:
                    mFingerDownY = (int) e.getRawY();
                    break;*/
                case MotionEvent.ACTION_MOVE:
                    // 解决下拉刷新自动滚动问题
                    /*if(mCurrentDrag){
                        scrollToPosition(0);
                    }*/

                    //计算距离
                    int distanceY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);


                    if(distanceY > 0){
                        int marginTop = - mRefreshViewHeight + distanceY;
                        //更新头部位置并更新状态
                        setRefreshViewMarginTop(marginTop);
                        //更新状态并回调
                        updateRefreshStatus(marginTop);
                        mCurrentDrag = true;
                        return false;
                    }
                    break;
            }
        }
        //mCurrentRefreshStatus = REFRESH_STATUS_NORMAL;
        return super.onTouchEvent(e);
    }

    /**
     * 判断是否可以下滑
     * @param event
     * @return
     */
    private boolean isAbleToScroll(MotionEvent event) {
        View firstNoRefreshChild = getChildAt(1);
        if(firstNoRefreshChild != null){
            if(firstNoRefreshChild.getTop() >= 0){
                if(!isAbleToPull){
                    mFingerDownY = (int) event.getRawY();
                }
                isAbleToPull = true;
            }else{
                isAbleToPull = false;
            }
        }else{
            isAbleToPull = true;
        }
        /*if(!canScrollUp()){
            if(!isAbleToPull){
                mFingerDownY = (int) event.getRawY();
            }
            isAbleToPull = true;
        }else{
            isAbleToPull = false;
        }
        //Log.e("能否滑动",!canScrollUp()+"");*/

        return isAbleToPull;
    }

    /**
     * 更新状态
     * @param marginTop
     */
    private void updateRefreshStatus(int marginTop) {
        if(marginTop <= - mRefreshViewHeight + 1){

            mCurrentRefreshStatus = REFRESH_STATUS_NORMAL;
            if(mRefreshCreator != null){
                mRefreshCreator.onFinished();
            }
        }else if(marginTop < 0){

            mCurrentRefreshStatus = REFRESH_STATUS_PULL_DOWN_REFRESH;
            if(mRefreshCreator != null){
                mRefreshCreator.onPull(mRefreshViewHeight + marginTop, mRefreshViewHeight, mCurrentRefreshStatus);
            }
        }else{

            mCurrentRefreshStatus = REFRESH_STATUS_LOOSEN_REFRESHING;
            if(mRefreshCreator != null){
                mRefreshCreator.onPull(mRefreshViewHeight + marginTop, mRefreshViewHeight, mCurrentRefreshStatus);
            }
        }
    }

    public boolean canScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return ViewCompat.canScrollVertically(this, -1) || this.getScrollY() > 0;
        } else {
            return ViewCompat.canScrollVertically(this, -1);
        }
    }

    /**
     * 状态回弹,包括回弹到初始和回弹到刷新
     */
    private void restoreRefreshView() {
        int currentTopMargin = ((MarginLayoutParams)(mRefreshView.getLayoutParams())).topMargin;
        int finalTopMargin = - mRefreshViewHeight + 1;

        //如果此时是松开可刷新状态
        if(mCurrentRefreshStatus == REFRESH_STATUS_LOOSEN_REFRESHING){
            isRollingToRefreshing = true;
            finalTopMargin = 0;
        }

        int distance = currentTopMargin - finalTopMargin;

        //利用属性动画进行弹性滑动
        ValueAnimator animator = ObjectAnimator.ofFloat(currentTopMargin, finalTopMargin)
                .setDuration(distance);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentTopMargin = (float) animation.getAnimatedValue();
                setRefreshViewMarginTop((int)currentTopMargin);
                //如果是回滚到初始状态
                if(isRollingToFInished){
                    if (mRefreshCreator != null) {
                        mRefreshCreator.onStoppingRefresh((int) (mRefreshViewHeight + currentTopMargin), mRefreshViewHeight);
                    }
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAutoScroll = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAutoScroll = false;
                if(isRollingToRefreshing){
                    mCurrentRefreshStatus = REFRESH_STATUS_REFRESHING;
                    isRollingToRefreshing = false;
                    if(mRefreshCreator != null){
                        mRefreshCreator.onRefreshing();
                    }
                    if(mListener != null){
                        mListener.onRefresh();
                    }
                } else if(isRollingToFInished){
                    mCurrentRefreshStatus = REFRESH_STATUS_NORMAL;
                    isRollingToFInished = false;
                    mRefreshCreator.onFinished();
                }
            }
        });
        animator.start();
        mCurrentDrag = false;
    }

    /**
     * 设置marginTop
     * @param currentTopMargin
     */
    private void setRefreshViewMarginTop(int currentTopMargin) {
        MarginLayoutParams params = (MarginLayoutParams) mRefreshView.getLayoutParams();
        if(currentTopMargin <= - mRefreshViewHeight + 1){
            currentTopMargin = - mRefreshViewHeight + 1;
        }

        params.topMargin = currentTopMargin;
        mRefreshView.setLayoutParams(params);
    }


    /**
     * 停止刷新
     */
    public void onStoppingRefresh() {
        if(mCurrentRefreshStatus == REFRESH_STATUS_REFRESHING){
            isRollingToFInished = true;
            restoreRefreshView();
        }
    }
}
