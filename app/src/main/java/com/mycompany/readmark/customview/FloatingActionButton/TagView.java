package com.mycompany.readmark.customview.FloatingActionButton;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *  在TabTagLayout中需要一个能设置Text的CardView
 *  所以继承CardView实现一个TagView
 *
 */
public class TagView extends CardView {
    private TextView mTextView;

    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextView = new TextView(context);
        mTextView.setSingleLine(true);
    }
    
    protected void setTextSize(float size){
        mTextView.setTextSize(size);
    }

    protected void setTextColor(int color){
        mTextView.setTextColor(color);
    }

    
    protected void setTagText(String text){
        mTextView.setText(text);
        addTag();
    }

    private void addTag(){
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        int l = dp2px(8);
        int t = dp2px(8);
        int r = dp2px(8);
        int b = dp2px(8);
        layoutParams.setMargins(l, t, r, b);
        //addView会引起所有View的layout
        addView(mTextView, layoutParams);
    }

    private int dp2px(int value){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , value, getResources().getDisplayMetrics());
    }

}