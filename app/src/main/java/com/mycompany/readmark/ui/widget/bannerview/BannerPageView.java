package com.mycompany.readmark.ui.widget.bannerview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo.
 */

public class BannerPageView extends ViewPager {
    private final static String TAG = "BannerPageView";
    private BaseAdapter mAdapter;

    //发送消息的message
    private final int SCROLL_MSG = 0x0011;

    //页面切换间隔时间
    private int mCountDownTime = 4000;


    private BannerScroller mScroller;

    //复用销毁的界面
    private List<View> mConvertViews;


    private Activity mActivity;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //Log.e(TAG, "mHandler新建了");
            setCurrentItem(getCurrentItem() + 1);
            startRoll();
        }
    };

    public BannerPageView(Context context) {
        this(context, null);
    }

    public BannerPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        mScroller = new BannerScroller(context);
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            //因为是私有变量
            field.setAccessible(true);
            //第一个参数代表当前属性在哪个类，第二个参数代表参数要设置的值
            field.set(this, mScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mConvertViews = new ArrayList<>();
    }


    public void setAdapter(BaseAdapter adapter) {
        setAdapter(new BannerPagerAdapter());
        mAdapter = adapter;
        //监听Activity生命周期
        mActivity.getApplication().registerActivityLifecycleCallbacks(mActivityCallbacks);
    }

    public void startRoll(){
        //Log.e(TAG, "startRoll");
        mHandler.sendEmptyMessageDelayed(SCROLL_MSG, mCountDownTime);
        //resetRoll();
    }

    public void setScrollerDuaration(int duaration) {
        mScroller.setScrollerDuaration(duaration);
        resetRoll();
    }

    public void setScrollerCountDown(int countDown){
        mCountDownTime = countDown;
        resetRoll();
    }

    public void resetRoll(){
        mHandler.removeMessages(SCROLL_MSG);
        mHandler.sendEmptyMessageDelayed(SCROLL_MSG, mCountDownTime);
    }


    private class BannerPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View bannerItemView = mAdapter.getView(position % mAdapter.getCount(), getConvertView(position), container);
            container.addView(bannerItemView);
            return bannerItemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

            if(!mConvertViews.contains((View) object)){
                mConvertViews.add((View) object);
            }
        }
    }


    private View getConvertView(int position) {
        int size = mConvertViews.size();
        //Log.e("mConvertViews", "mConvertViews size:" + mConvertViews.size());
        for(int i=0; i<size; i++){
            if(mConvertViews.get(i).getParent() == null){
                return mConvertViews.get(i);
            }
        }
        return null;
    }
    /*
    * 防止内存泄漏
    * */
    @Override
    protected void onDetachedFromWindow() {
        /*Log.e(TAG, "onDetachedFromWindow");
        if(mHandler != null){
            mHandler.removeMessages(SCROLL_MSG);
            mHandler = null;
        }
        if(mActivity != null){
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(mActivityCallbacks);
        }*/
        super.onDetachedFromWindow();
    }

    /*
    * Activity没有显示的时候逻辑上是不需要进行切换的
    *
    * */
    private Application.ActivityLifecycleCallbacks mActivityCallbacks = new SimpleActivityCallbacks(){
        @Override
        public void onActivityResumed(Activity activity) {
            super.onActivityResumed(activity);
            Log.e(TAG,"onActivityResumed");
            if(activity == mActivity){
                mHandler.sendEmptyMessageDelayed(mCountDownTime, SCROLL_MSG);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            super.onActivityPaused(activity);
            if(activity == mActivity) {
                mHandler.removeMessages(SCROLL_MSG);
            }
        }
    };
}
