package com.mycompany.readmark.ui.adapter.commen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Lenovo.
 */

public class LoadRefreshRecyclerView extends RefreshRecyclerView {

    // 上拉加载更多的辅助类
    private LoadViewCreator mLoadCreator;

    // View高度
    private int mLoadViewHeight = 0;

    // 上拉加载更多的头部View
    private View mLoadView;

    // 手指按下的Y位置
    private int mFingerDownY;

    // 当前是否正在拖动
    private boolean mCurrentDrag = false;



    // 默认状态
    public int LOAD_STATUS_NORMAL = 0x0011;

    // 上拉加载更多状态
    public static int LOAD_STATUS_PULL_DOWN_REFRESH = 0x0022;

    // 松开加载更多状态
    public static int LOAD_STATUS_LOOSEN_LOADING = 0x0033;

    // 正在加载更多状态
    public int LOAD_STATUS_LOADING = 0x0044;

    // 当前的状态
    private int mCurrentLoadStatus = LOAD_STATUS_NORMAL;


    // 处理加载更多回调监听
    private OnLoadMoreListener mListener;
    private boolean isAutoScroll;
    private boolean isTmpFirstDown = true;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoad();
    }



    public LoadRefreshRecyclerView(Context context) {
        super(context);
    }

    public LoadRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * 为当前RV添加一个用于上啦刷新的View
     * @param loadCreator
     */
    public void addLoadViewCreator(LoadViewCreator loadCreator) {
        this.mLoadCreator = loadCreator;
        addRefreshView();
    }

    /**
     * 如果Adapter改变，那么也要重新添加这个View，因为Adapter会被重新包装
     * @param adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        addRefreshView();
    }

    /**
     * 添加底部加载更多View，作为一个Footer
     */
    private void addRefreshView() {
        Adapter adapter = getAdapter();
        if (adapter != null && mLoadCreator != null) {
            // 添加底部加载更多View
            View loadView = mLoadCreator.getLoadView(getContext(), this);
            if (loadView != null) {
                addFooterView(loadView);
                this.mLoadView = loadView;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取一次手指按下的位置
                mFingerDownY = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                if (mCurrentDrag) {
                    restoreLoadView();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * UP后的位置恢复，根据不同的状态来决定
     */
    private void restoreLoadView() {
        if(mCurrentLoadStatus == LOAD_STATUS_LOADING){
            mCurrentLoadStatus = LOAD_STATUS_NORMAL;
            if (mLoadCreator != null) {
                mLoadCreator.onFinished();
            }
        }

        int currentBottomMargin = ((MarginLayoutParams) mLoadView
                .getLayoutParams()).bottomMargin;
        int finalBottomMargin = 0;

        //只有一种可能
        int distance = currentBottomMargin - finalBottomMargin;

        // 回弹到指定位置
        ValueAnimator animator = ObjectAnimator.ofFloat(currentBottomMargin, finalBottomMargin)
                .setDuration(distance);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentTopMargin = (float) animation.getAnimatedValue();
                setLoadViewMarginBottom((int) currentTopMargin);

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
                if (mCurrentLoadStatus == LOAD_STATUS_LOOSEN_LOADING) {
                    mCurrentLoadStatus = LOAD_STATUS_LOADING;
                    if (mLoadCreator != null) {
                        mLoadCreator.onLoading();
                    }
                    if (mListener != null) {
                        mListener.onLoad();
                    }
                }else if(mCurrentLoadStatus == LOAD_STATUS_PULL_DOWN_REFRESH){
                    mCurrentLoadStatus = LOAD_STATUS_NORMAL;
                    if (mLoadCreator != null) {
                        mLoadCreator.onFinished();
                    }
                }
            }
        });
        animator.start();
        mCurrentDrag = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(mCurrentLoadStatus == LOAD_STATUS_LOADING || isAutoScroll){
            //Log.e(TAG, "在刷新状态进入");
            return true;
        }else if(!canScrollDown()){
            //能滚动的时候初次记录Y
            if(isTmpFirstDown){
                mFingerDownY = (int) e.getRawY();
                isTmpFirstDown = false;
            }
            switch(e.getAction()){
                case MotionEvent.ACTION_MOVE:
                    if (mLoadCreator != null) {
                        mLoadViewHeight = mLoadView.getMeasuredHeight();
                    }
                    // 解决上拉加载更多自动滚动问题
                    /*if (mCurrentDrag) {
                        scrollToPosition(getAdapter().getItemCount() - 1);
                    }*/

                    // 获取手指触摸拖拽的距离
                    int distanceY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);
                    // 如果是已经到达头部，并且不断的向下拉，那么不断的改变refreshView的marginTop的值
                    if (distanceY < 0) {
                        //和底部的margin是-Y
                        setLoadViewMarginBottom(-distanceY);
                        updateLoadStatus(-distanceY);
                        mCurrentDrag = true;
                        return true;
                    }
                    break;
            }
        }

        return super.onTouchEvent(e);
    }

    /**
     * 更新加载的状态
     */
    private void updateLoadStatus(int distanceY) {
        if (distanceY <= 0) {
            mCurrentLoadStatus = LOAD_STATUS_NORMAL;
            if (mLoadCreator != null) {
                mLoadCreator.onFinished();
            }
        } else if (distanceY < mLoadViewHeight) {
            mCurrentLoadStatus = LOAD_STATUS_PULL_DOWN_REFRESH;
            if (mLoadCreator != null) {
                mLoadCreator.onPull(distanceY, mLoadViewHeight, mCurrentLoadStatus);
            }
        } else {
            mCurrentLoadStatus = LOAD_STATUS_LOOSEN_LOADING;
            if (mLoadCreator != null) {
                mLoadCreator.onPull(distanceY, mLoadViewHeight, mCurrentLoadStatus);
            }
        }

    }


    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     * 判断是不是滚动到了最顶部，这个是从SwipeRefreshLayout里面copy过来的源代码
     */
    public boolean canScrollDown() {
        return ViewCompat.canScrollVertically(this, 1);
    }

    /**
     * 设置加载View的marginBottom
     */
    public void setLoadViewMarginBottom(int marginBottom) {
        MarginLayoutParams params = (MarginLayoutParams) mLoadView.getLayoutParams();
        if (marginBottom < 0) {
            marginBottom = 0;
        }
        params.bottomMargin = marginBottom;
        mLoadView.setLayoutParams(params);
    }

    /**
     * 停止加载更多
     */
    public void onStoppingLoad() {
        if(mCurrentLoadStatus == LOAD_STATUS_LOADING){
            restoreLoadView();
        }
    }
}
