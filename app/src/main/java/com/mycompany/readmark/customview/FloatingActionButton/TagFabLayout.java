package com.mycompany.readmark.customview.FloatingActionButton;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.mycompany.readmark.R;

/**
 * 该ViewGroup是点击Tab后展开的条目
 * 组合一个TagView和一个FloatingActionButton
 * 默认Tag在左，button在右
 */
public class TagFabLayout extends ViewGroup {
    private String mTagText;
    private TagView mTagView;

    private OnTagClickListener mOnTagClickListener;
    private OnFabClickListener mOnFabClickListener;

    public interface OnTagClickListener{
        void onTagClick();
    }

    public interface OnFabClickListener{
        void onFabClick();
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        mOnTagClickListener = onTagClickListener;
    }

    public void setOnFabClickListener(OnFabClickListener onFabClickListener) {
        mOnFabClickListener = onFabClickListener;
    }

    public TagFabLayout(Context context) {
        this(context, null);
    }

    public TagFabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
        //除了从布局加载FloatingActionButton，还要添加一个TagView
        settingTagView(context);
    }

    private void getAttributes(Context context, AttributeSet attributeSet){
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet
                , R.styleable.FabTagLayout);
        mTagText = typedArray.getString(R.styleable.FabTagLayout_tagText);
        typedArray.recycle();
    }

    private void settingTagView(Context context){
        mTagView = new TagView(context);
        mTagView.setTagText(mTagText);
        addView(mTagView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int initWidth = MeasureSpec.getSize(widthMeasureSpec);
        int initHeight = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;

        int count = getChildCount();
        for(int i=0; i<count; i++){

            View view = getChildAt(i);

            measureChild(view, widthMeasureSpec, heightMeasureSpec);

            width += view.getMeasuredWidth();
            height = Math.max(height, view.getMeasuredHeight());
        }

        width += dp2px(8 + 8 + 8);
        height += dp2px(8 + 8);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("OnMeasure中测得的结果是", getMeasuredWidth() + "*" + getMeasuredHeight());
        //为TabLayout重新布局，因为在onMeasure中没有支持wrap_content
        //而这个ViewGroup从效果上来说必须是wrap_content
        /*int width = getChildAt(0).getMeasuredWidth()
                + getChildAt(1).getMeasuredWidth() + dp2px(24 + 8 + 8);
        int height = Math.max(getChildAt(0).getMeasuredHeight()
                , getChildAt(1).getMeasuredHeight()) + dp2px(12);
        LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);*/

        //为子View布局
        View tagView = getChildAt(0);
        View fabView = getChildAt(1);

        int tagWidth = tagView.getMeasuredWidth();
        int tagHeight = tagView.getMeasuredHeight();
        Log.e("Tag的宽高",tagWidth+"*"+tagHeight);
        int fabWidth = fabView.getMeasuredWidth();
        int fabHeight = fabView.getMeasuredHeight();
        Log.e("Tag旁边fab的宽高",fabWidth+"*"+fabHeight);

        int tl = dp2px(8);
        int tt = (getMeasuredHeight() - tagHeight) / 2;
        int tr = tl + tagWidth;
        int tb = tt + tagHeight;

        int fl = tr + dp2px(8);
        int ft = (getMeasuredHeight() - fabHeight) / 2;
        int fr = fl + fabWidth;
        int fb = ft + fabHeight;

        fabView.layout(fl, ft, fr, fb);
        tagView.layout(tl, tt, tr, tb);
        bindEvents(tagView, fabView);
    }

    private void bindEvents(View tagView, View fabView){
        tagView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnTagClickListener != null){
                    mOnTagClickListener.onTagClick();
                }
            }
        });

        fabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFabClickListener != null){
                    mOnFabClickListener.onFabClick();
                }
            }
        });
    }


    private int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , value, getResources().getDisplayMetrics());

    }
}