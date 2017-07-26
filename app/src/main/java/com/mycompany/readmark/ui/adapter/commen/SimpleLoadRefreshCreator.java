package com.mycompany.readmark.ui.adapter.commen;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycompany.readmark.R;

import org.w3c.dom.Text;

/**
 * Created by Lenovo.
 */

public class SimpleLoadRefreshCreator extends LoadViewCreator{
    private View mMainview;
    private TextView mTextView;


    public SimpleLoadRefreshCreator(Context context, ViewGroup parent) {
        mMainview = LayoutInflater.from(context).inflate(R.layout.just_a_refresh_footer, parent, false);
        mTextView = (TextView) mMainview.findViewById(R.id.footer_tv);
        mTextView.setText("上拉刷新");
    }

    @Override
    public View getLoadView(Context context, ViewGroup parent) {
        return mMainview;
    }

    @Override
    public void onPull(int currentDragHeight, int loadViewHeight, int currentLoadStatus) {
        if(currentDragHeight <= loadViewHeight){
            mTextView.setText("继续上拉以刷新");
        }else if(currentDragHeight > loadViewHeight){
            mTextView.setText("释放以刷新");
        }
    }

    @Override
    public void onLoading() {
        mTextView.setText("正在刷新...");

        /*mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(-1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                mImageView.setRotation(360 * factor);
            }
        });*/

    }

    @Override
    public void onStopLoading() {

    }

    @Override
    public void onFinished() {
        //mTextView.setVisibility(View.VISIBLE);
        mTextView.setText("上拉刷新");
        //mImageView.setVisibility(View.INVISIBLE);
    }
}
