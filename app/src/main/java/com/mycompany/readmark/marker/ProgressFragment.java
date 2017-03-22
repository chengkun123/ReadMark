package com.mycompany.readmark.marker;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.mycompany.readmark.R;
import com.mycompany.readmark.customview.PercentView.WaveLoadingView;

/**
 * Created by Lenovo on 2017/3/19.
 */
public class ProgressFragment extends Fragment {

    private WaveLoadingView mWaveLoadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, null);
        mWaveLoadingView = (WaveLoadingView) view.findViewById(R.id.wave_view);
        mWaveLoadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Bundle bundle = getArguments();
        MarkerBean bean = (MarkerBean) bundle.getSerializable("bean");
        setPercent();

        //TextView textView = (TextView) view.findViewById(R.id.progress_text);
        //textView.setText(bean.getProgress()+"");

        return view;
    }

    private void setPercent(){
        if(mWaveLoadingView.getFirstLoading()){
            ValueAnimator animator = ValueAnimator.ofInt(0, 90);
            animator.setDuration(1000);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mWaveLoadingView.setPercent((int) animation.getAnimatedValue());
                }
            });
            animator.start();
            mWaveLoadingView.setFirstLoad(false);
        }
    }


}
