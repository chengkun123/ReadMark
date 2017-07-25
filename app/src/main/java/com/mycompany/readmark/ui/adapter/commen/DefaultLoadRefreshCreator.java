package com.mycompany.readmark.ui.adapter.commen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.mycompany.readmark.R;

/**
 * Created by Lenovo.
 */

public class DefaultLoadRefreshCreator extends LoadViewCreator {
    private View mRefreshIv;

    @Override
    public View getLoadView(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.just_a_refresh_header, parent, false);
        mRefreshIv = view.findViewById(R.id.refresh_image);
        return view;
    }

    @Override
    public void onPull(int currentDragHeight, int loadViewHeight, int currentLoadStatus) {
        mRefreshIv.setRotation(((float) currentDragHeight) / (float)loadViewHeight);
    }

    @Override
    public void onLoading() {
        // 刷新的时候不断旋转
        RotateAnimation animation = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        mRefreshIv.startAnimation(animation);
    }

    @Override
    public void onStopLoad() {
        // 停止加载的时候清除动画
        mRefreshIv.setRotation(0);
        mRefreshIv.clearAnimation();
    }
}
