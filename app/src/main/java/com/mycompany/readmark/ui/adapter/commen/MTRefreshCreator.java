package com.mycompany.readmark.ui.adapter.commen;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycompany.readmark.R;
import com.mycompany.readmark.ui.widget.MTheader.MTFirstView;
import com.mycompany.readmark.ui.widget.MTheader.MTSecondView;
import com.mycompany.readmark.ui.widget.MTheader.MTThirdView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Lenovo.
 */

public class MTRefreshCreator extends RefreshViewCreator{
    private View mHeader;
    private MTFirstView mFirstStepView;
    private MTSecondView mSecondStepView;
    private MTThirdView mThirdStepView;
    private TextView mHeaderText;
    private AnimationDrawable mSecondAnimIn;
    private AnimationDrawable mThirdAnim;

    public MTRefreshCreator(Context context, ViewGroup parent) {
        mHeader = LayoutInflater.from(context)
                .inflate(R.layout.just_a_mt_refresh_header, parent, false);
        mFirstStepView = (MTFirstView) mHeader.findViewById(R.id.first_view);
        mSecondStepView = (MTSecondView) mHeader.findViewById(R.id.second_view);
        mThirdStepView = (MTThirdView) mHeader.findViewById(R.id.third_view);
        mHeaderText = (TextView) mHeader.findViewById(R.id.tv_pull_to_refresh);
        mSecondStepView.setBackgroundResource(R.drawable.mt_pull_to_refresh_second_anim_in);
        mThirdStepView.setBackgroundResource(R.drawable.mt_pull_to_refresh_third_anim);
        //获取补间动画
        mSecondAnimIn = (AnimationDrawable) mSecondStepView.getBackground();
        mThirdAnim = (AnimationDrawable) mThirdStepView.getBackground();
    }

    @Override
    public View getRefreshView(Context context, ViewGroup parent) {
        return mHeader;
    }

    /**
     * 停止（初始阶段）阶段
     */
    @Override
    public void onFinished() {
        mFirstStepView.setVisibility(GONE);
        mSecondStepView.setVisibility(GONE);
        mSecondAnimIn.stop();
        mThirdStepView.setVisibility(GONE);
        mThirdAnim.stop();
        mHeaderText.setText("");
    }

    /**
     * 下拉阶段
     * @param currentDragHeight    当前拖动的高度
     * @param refreshViewHeight    总的刷新高度
     * @param currentRefreshStatus 当前状态
     */
    @Override
    public void onPull(int currentDragHeight, int refreshViewHeight, int currentRefreshStatus) {
        if(currentDragHeight < refreshViewHeight){
            mFirstStepView.setVisibility(VISIBLE);
            mFirstStepView.setProgress((float)currentDragHeight / (float)refreshViewHeight);
            mSecondStepView.setVisibility(GONE);
            mSecondAnimIn.stop();
            mThirdStepView.setVisibility(GONE);
            mThirdAnim.stop();
            mHeaderText.setText("继续下拉以刷新");
        }else{
            mFirstStepView.setVisibility(GONE);
            mSecondStepView.setVisibility(VISIBLE);
            mSecondAnimIn.start();
            mThirdStepView.setVisibility(GONE);
            mThirdAnim.stop();
            mHeaderText.setText("释放以刷新");
        }
    }


    /**
     * 正在刷新阶段
     */
    @Override
    public void onRefreshing() {
        mFirstStepView.setVisibility(GONE);
        mSecondStepView.setVisibility(GONE);
        mSecondAnimIn.stop();
        mThirdStepView.setVisibility(VISIBLE);
        mThirdAnim.start();
        mHeaderText.setText("正在刷新");
    }

    /**
     * 正在停止刷新，也就是还原到初始位置
     */
    @Override
    public void onStoppingRefresh(int currentDragHeight, int refreshViewHeight) {
        mFirstStepView.setVisibility(VISIBLE);
        mFirstStepView.setProgress((float)currentDragHeight / (float)refreshViewHeight);
        mSecondStepView.setVisibility(GONE);
        mSecondAnimIn.stop();
        mThirdStepView.setVisibility(GONE);
        mThirdAnim.stop();
        mHeaderText.setText("");
    }
}
