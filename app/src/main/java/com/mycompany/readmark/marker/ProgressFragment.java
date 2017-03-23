package com.mycompany.readmark.marker;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.mycompany.readmark.R;
import com.mycompany.readmark.common.DatabaseTableSingleton;
import com.mycompany.readmark.customview.PercentView.WaveLoadingView;

/**
 * Created by Lenovo on 2017/3/19.
 */
public class ProgressFragment extends Fragment
        implements PercentSavingDialogFragment.OnPositiveClickListener{

    private WaveLoadingView mWaveLoadingView;
    private MarkerBean mMarker;
    private OnPercentChangedListener mOnPercentChangedListener;

    public interface OnPercentChangedListener{
        void onPercentChanged();
    }

    public void setOnPercentChangedListener(OnPercentChangedListener onPercentChangedListener){
        mOnPercentChangedListener = onPercentChangedListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, null);
        mWaveLoadingView = (WaveLoadingView) view.findViewById(R.id.wave_view);

        mWaveLoadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                //Toast.makeText(getActivity(), "点击了水波纹", Toast.LENGTH_SHORT).show();
            }
        });
        Bundle bundle = getArguments();
        mMarker = (MarkerBean) bundle.getSerializable("bean");
        resetPercent(mMarker.getProgress());

        //TextView textView = (TextView) view.findViewById(R.id.progress_text);
        //textView.setText(bean.getProgress()+"");

       /* setOnPercentChangedListener((OnPercentChangedListener)
                getActivity().getSupportFragmentManager().getBackStackEntryAt(0));*/

        return view;
    }




    private void resetPercent(float percent){
        ValueAnimator animator;
        if(mWaveLoadingView.getFirstLoading()){
            animator = ValueAnimator.ofFloat(0, percent);
            mWaveLoadingView.setFirstLoad(false);
        }else{
            animator = ValueAnimator.ofFloat(mWaveLoadingView.getPercent(), percent);
        }
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaveLoadingView.setPercent((float)animation.getAnimatedValue());
            }
        });
        animator.start();
    }
    private void showDialog(){
        PercentSavingDialogFragment fragment = new PercentSavingDialogFragment();
        fragment.setOnPositiveClickListener(this);
        fragment.show(getFragmentManager(), "percent");

    }

    @Override
    public void onPositiveClick(String text) {

        int total = Integer.parseInt(mMarker.getPages().replaceAll("[\\D]", ""));
        Log.e("total",total+"");
        int alreadyRead = (int)mMarker.getProgress() * total;
        Log.e("alreadyRead","初始的："+alreadyRead);

        try {
            alreadyRead = Integer.parseInt(text.replaceAll("[\\D]", ""));
            Log.e("alreadyRead","重设的："+alreadyRead);
        } catch(NumberFormatException nfe) {
            Toast.makeText(getActivity(), "输入数字错误，请重新输入！", Toast.LENGTH_SHORT).show();
        }

        float percent = alreadyRead / (float) total;
        Log.e("total","已读百分比"+percent);

        if(alreadyRead >= total){
            percent = 1f;
            DatabaseTableSingleton.getDatabaseTable(getActivity())
                    .updateMarkerPercent(1f, mMarker);
            resetPercent(percent);
        }else{
            DatabaseTableSingleton.getDatabaseTable(getActivity())
                    .updateMarkerPercent(percent, mMarker);
            resetPercent(percent);
        }

        //mOnPercentChangedListener.onPercentChanged();
    }




}
