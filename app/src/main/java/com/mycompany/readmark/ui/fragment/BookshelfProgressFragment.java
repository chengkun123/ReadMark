package com.mycompany.readmark.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.readmark.R;
import com.mycompany.readmark.api.presenter.impl.BookshelfPresenterImpl;
import com.mycompany.readmark.api.view.IBookListView;
import com.mycompany.readmark.bean.table.Bookshelf;
import com.mycompany.readmark.holder.WaveLoadingViewHolder;
import com.mycompany.readmark.themechangeframeV2.skin.SkinManager;
import com.mycompany.readmark.themechangeframeV2.skin.callback.ISkinChangeListener;
import com.mycompany.readmark.ui.widget.WaveLoadingView;

/**
 * Created by Lenovo.
 */

public class BookshelfProgressFragment extends BaseFragment implements IBookListView {

    private Bookshelf mBookshelf;
    private BookshelfPresenterImpl mBookshelfPresenterImpl;
    private WaveLoadingView mWaveLoadingView;
    private SeekBar mRedPicker;
    private SeekBar mGreenPicker;
    private SeekBar mBluePicker;
    private SeekBar mAmpPicker;
    private SeekBar mWavePicker;
    private EditText mCurrentPage;
    private TextView mTotalPages;
    private Button mConfirmButton;
    //private Button mCancelButton;



    private int mRed;
    private int mGreen;
    private int mBlue;
    private int mColor;

    private boolean isDateQualified = true;


    public void setOnProgressConfirmedlistener(OnProgressConfirmedListener listener){

    }


    public static BookshelfProgressFragment newInstance(Bookshelf bookshelf) {
        Bundle args = new Bundle();
        args.putSerializable("progress", bookshelf);
        BookshelfProgressFragment fragment = new BookshelfProgressFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*WaveLoadingViewHolder holder =
                new WaveLoadingViewHolder(getActivity()
                        , (Bookshelf) getArguments().getSerializable("progress")
                        , container);*/


        mRootView = LayoutInflater.from(getActivity())
                .inflate(R.layout.item_wave_loading_view, container, false);


    }

    @Override
    protected void initEvents() {
        mBookshelf = (Bookshelf) getArguments().getSerializable("progress");
        mBookshelfPresenterImpl = new BookshelfPresenterImpl(this);
        mWaveLoadingView = (WaveLoadingView) mRootView.findViewById(R.id.wave_loading_view);
        mWaveLoadingView.post(new Runnable() {
            @Override
            public void run() {
                mWaveLoadingView.setWaveColor(mBookshelf.getColor());
            }
        });
        mRedPicker = (SeekBar) mRootView.findViewById(R.id.red_picker);
        mGreenPicker = (SeekBar) mRootView.findViewById(R.id.green_picker);
        mBluePicker = (SeekBar) mRootView.findViewById(R.id.blue_picker);
        mAmpPicker = (SeekBar) mRootView.findViewById(R.id.amp_picker);
        mWavePicker = (SeekBar) mRootView.findViewById(R.id.wave_picker);
        mCurrentPage = (EditText) mRootView.findViewById(R.id.edit_current_page);
        mTotalPages = (TextView) mRootView.findViewById(R.id.text_total_page);
        mConfirmButton = (Button) mRootView.findViewById(R.id.button_confirm);
        //mCancelButton = (Button) mRootView.findViewById(R.id.button_cancel);


        mRedPicker.post(new Runnable() {
            @Override
            public void run() {
                mRedPicker.setProgress( (int)((mBookshelf.getRed() / 255.0f) * 100));
            }
        });
        mGreenPicker.post(new Runnable() {
            @Override
            public void run() {
                mGreenPicker.setProgress((int)((mBookshelf.getGreen() / 255.0f) * 100));
            }
        });
        mBluePicker.post(new Runnable() {
            @Override
            public void run() {
                mBluePicker.setProgress((int)((mBookshelf.getBlue() / 255.0f) * 100));
            }
        });
        mAmpPicker.post(new Runnable() {
            @Override
            public void run() {
                //范围是0~1
                mAmpPicker.setProgress((int) (mBookshelf.getAmpratio() * 100));
            }
        });
        mWavePicker.post(new Runnable() {
            @Override
            public void run() {
                mWavePicker.setProgress((int) (mBookshelf.getWaveratio() * 100));
            }
        });
        mCurrentPage.post(new Runnable() {
            @Override
            public void run() {
                mCurrentPage.setText(""+mBookshelf.getCurrentpage());
            }
        });
        mTotalPages.post(new Runnable() {
            @Override
            public void run() {
                mTotalPages.setText("of "+mBookshelf.getTotalpage());
            }
        });
        //mWaveLoadingView.setProgress(mBookshelf.getProgress());
        Log.e("此时progress是",mBookshelf.getProgress()+"");
        mWaveLoadingView.setTitletext(((int)(mBookshelf.getProgress() * 100)) + "" + " %");



        mRedPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRed = (int) ((float)progress / seekBar.getMax() * 255);
                mBookshelf.setRed(mRed);
                mColor = Color.rgb(mRed, mGreen, mBlue);
                mBookshelf.setColor(mColor);
                mWaveLoadingView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWaveLoadingView.setWaveColor(mColor);
                    }
                });

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mGreenPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGreen = (int) ((float)progress / seekBar.getMax() * 255);
                mBookshelf.setGreen(mGreen);
                mColor = Color.rgb(mRed, mGreen, mBlue);
                mBookshelf.setColor(mColor);
                mWaveLoadingView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWaveLoadingView.setWaveColor(mColor);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBluePicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBlue = (int) ((float)progress / seekBar.getMax() * 255);
                mBookshelf.setBlue(mBlue);
                mColor = Color.rgb(mRed, mGreen, mBlue);
                mBookshelf.setColor(mColor);
                mWaveLoadingView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWaveLoadingView.setWaveColor(mColor);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mWavePicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveLoadingView.setWaveLengthRatio((float)progress / seekBar.getMax());
                mBookshelf.setWaveratio((float)progress / seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mAmpPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveLoadingView.setWaveAmplitudeRatio((float)progress / seekBar.getMax());
                mBookshelf.setAmpratio((float)progress / seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mCurrentPage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    //获取有效数字
                    int alreadyRead = Integer.parseInt(s.toString().replaceAll("[\\D]", ""));
                    isDateQualified = true;
                    /*
                    * 封闭起来避免死循环
                    * */
                    if(alreadyRead > mBookshelf.getTotalpage()){
                        alreadyRead = mBookshelf.getTotalpage();
                        mCurrentPage.setText(""+alreadyRead);
                    }else if(alreadyRead < 0){
                        alreadyRead = 0;
                        mCurrentPage.setText(""+alreadyRead);
                    }
                    mBookshelf.setCurrentpage(alreadyRead);
                    mBookshelf.setProgress((float)alreadyRead / mBookshelf.getTotalpage());
                    mWaveLoadingView.setProgress((float)alreadyRead / mBookshelf.getTotalpage());
                    mWaveLoadingView.setTitletext(((int)(mBookshelf.getProgress() * 100)) + "" + " %");
                    Log.e("此时progress是",mBookshelf.getProgress()+"");
                    if(alreadyRead == mBookshelf.getTotalpage()){
                        mBookshelf.setFinished(1);
                    }else{
                        mBookshelf.setFinished(0);
                    }
                } catch(NumberFormatException nfe) {
                    //发生格式错误保存原来的值
                    isDateQualified = false;
                    mWaveLoadingView.setProgress((float)mBookshelf.getCurrentpage() / mBookshelf.getTotalpage());
                    mWaveLoadingView.setTitletext(((int)(mBookshelf.getProgress() * 100)) + "" + " %");
                    Log.e("此时progress是",mBookshelf.getProgress()+"");
                    mBookshelf.setFinished(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBookshelfPresenterImpl.updateBookshelf(mBookshelf);
            }
        });


        /*mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

    }

    @Override
    protected void initData(boolean isSavedNull) {

    }


    /*
    *
    *
    * */
    @Override
    public void showMessage(String msg) {
        if(isDateQualified){
            Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "数据格式错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void refreshData(Object result) {

    }

    @Override
    public void addData(Object result) {

    }


    public interface OnProgressConfirmedListener{
        void onProgressConfirmed(Bookshelf bookshelf, boolean isQualified);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*SkinManager
                .getInstance()
                .clearRegisteredDetachedView((ISkinChangeListener) getActivity());*/
    }
}
