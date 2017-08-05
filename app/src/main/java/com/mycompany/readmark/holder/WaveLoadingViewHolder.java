package com.mycompany.readmark.holder;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Layout;
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
import com.mycompany.readmark.bean.table.Bookshelf;
import com.mycompany.readmark.ui.widget.WaveLoadingView;

/**
 * Created by Lenovo.
 */

public class WaveLoadingViewHolder {
    private Bookshelf mBookshelf;
    private Context mContext;
    private ViewGroup mViewGroup;


    private View mContentView;
    private WaveLoadingView mWaveLoadingView;
    private SeekBar mRedPicker;
    private SeekBar mGreenPicker;
    private SeekBar mBluePicker;
    private SeekBar mAmpPicker;
    private SeekBar mWavePicker;
    private EditText mCurrentPage;
    private TextView mTotalPages;
    private Button mConfirmButton;
    private Button mCancelButton;



    private int mRed;
    private int mGreen;
    private int mBlue;
    private int mColor;

    private boolean isDateQualified = true;

    public WaveLoadingViewHolder(Context context, Bookshelf bookshelf, ViewGroup container) {
        mViewGroup = container;
        mBookshelf = bookshelf;
        mContext = context;
        mRed = mBookshelf.getRed();
        mGreen = mBookshelf.getGreen();
        mBlue = mBookshelf.getBlue();
        mColor = mBookshelf.getColor();

        initViews();
        initEvents();
    }

    private void initViews(){
        mContentView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_wave_loading_view, mViewGroup, false);
        mWaveLoadingView = (WaveLoadingView) mContentView.findViewById(R.id.wave_loading_view);
        mWaveLoadingView.post(new Runnable() {
            @Override
            public void run() {
                mWaveLoadingView.setWaveColor(mBookshelf.getColor());
            }
        });
        mRedPicker = (SeekBar) mContentView.findViewById(R.id.red_picker);
        mGreenPicker = (SeekBar) mContentView.findViewById(R.id.green_picker);
        mBluePicker = (SeekBar) mContentView.findViewById(R.id.blue_picker);
        mAmpPicker = (SeekBar) mContentView.findViewById(R.id.amp_picker);
        mWavePicker = (SeekBar) mContentView.findViewById(R.id.wave_picker);
        mCurrentPage = (EditText) mContentView.findViewById(R.id.edit_current_page);
        mTotalPages = (TextView) mContentView.findViewById(R.id.text_total_page);
        mConfirmButton = (Button) mContentView.findViewById(R.id.button_confirm);
        //mCancelButton = (Button) mContentView.findViewById(R.id.button_cancel);


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


    }

    private void initEvents(){
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

            }
        });


        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public View getContentView() {
        return mContentView;
    }

    public boolean isDateQualified() {
        return isDateQualified;
    }


    public interface OnProgressConfirmedListener{
        void onProgressConfirmed(Bookshelf bookshelf, boolean isQualified);
    }



}
