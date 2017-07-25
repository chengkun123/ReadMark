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

public class DefaultRefreshCreator extends RefreshViewCreator {
    private View mRefreshIv;

    @Override
    public View getRefreshView(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.just_a_refresh_header, parent, false);
        mRefreshIv = view.findViewById(R.id.refresh_image);
        return view;
    }

    @Override
    public void onPull(int currentDragHeight, int refreshViewHeight, int currentRefreshStatus) {
        mRefreshIv.setRotation(((float) currentDragHeight) / (float)refreshViewHeight);
    }

    @Override
    public void onRefreshing() {
        // 刷新的时候不断旋转
        RotateAnimation animation = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        mRefreshIv.startAnimation(animation);
    }

    @Override
    public void onStopRefresh() {
        // 停止加载的时候清除动画
        mRefreshIv.setRotation(0);
        mRefreshIv.clearAnimation();
    }
}
