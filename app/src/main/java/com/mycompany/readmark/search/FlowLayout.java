package com.mycompany.readmark.search;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/1/11.
 */
public class FlowLayout extends ViewGroup {
    private List<List<View>> mViews = new ArrayList<>();
    private List<Integer> mLineHeight = new ArrayList<>();
    private AdapterDataSetObserver mDataSetObserver;
    private ListAdapter mAdapter;
    private SparseBooleanArray mCheckedTagArray = new SparseBooleanArray();;



    public FlowLayout(Context context) {
        this(context, null);
    }


    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //那么构造函数的逻辑都可以写在这个函数中
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //去看源码的话，我们可以发现如果子View是match_parent或wrap_content，父View传入的尺寸是父布局的尺寸
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //为了支持wrap_content属性，我们重新计算该ViewGroup的宽和高
        int width = 0;
        int height = 0;

        //记录每一行的宽度与高度
        int lineWidth = 0;
        int lineHeight = 0;

        int count = getChildCount();
        //支持父控件的padding属性
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        //计算在wrap_content情况下的宽高
        for(int i=0; i<count; i++){
            //对于每一个子View，进行测量
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //计算添加此child后的行宽。
            int tempWidth = lineWidth +  lp.leftMargin + childWidth + lp.rightMargin;

            //如果需要换行，注意要减去padding
            if (tempWidth > sizeWidth - paddingLeft -paddingRight){
                //更新width和height
                width = Math.max(lineWidth, width);
                height += lineHeight;

                //更新lineWidth和lineHeight
                lineWidth = childWidth;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;

            }else{//如果不需要换行
                //更新lineHeight和lineWidth
                lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
                lineWidth = tempWidth;
                //更新width(height不需要更新)
                width = Math.max(lineWidth, width);
            }
        } // end for
        //考虑最后一行
        height += lineHeight;
        width = Math.max(width, lineWidth);

        Log.e("控件宽度", "" + sizeWidth);
        Log.e("控件高度", ""+sizeHeight);
        //注意控件的宽度要加上padding
        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + paddingLeft + paddingRight,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + paddingTop + paddingBottom);


    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mViews.clear();
        mLineHeight.clear();

        int count = getChildCount();
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        List<View> row = new ArrayList<>();
        //支持父控件的padding属性
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        //为mViews和mLineHeight赋值
        for(int i=0; i<count; i++){
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int tempWidth = lineWidth + lp.leftMargin + childWidth + lp.rightMargin;

            //如果需要换行，注意要减去padding
            if(tempWidth > width - paddingLeft - paddingRight){
                mLineHeight.add(lineHeight);
                mViews.add(row);
                //把这个View放到下一行
                //不能使用clear()
                row = new ArrayList<>();
                row.add(child);
                lineWidth = childWidth;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
            }else{//如果不需要换行
                row.add(child);
                lineWidth = tempWidth;
                lineHeight = Math.max(lineHeight, childHeight+lp.topMargin+lp.bottomMargin);
            }
        }
        //将最后一行相关信息添加
        mViews.add(row);
        mLineHeight.add(lineHeight);

        //用于设置子View的位置，注意开始的位置
        int left = paddingLeft;
        int top = paddingTop;

        for(int i=0; i<mViews.size(); i++){

            for(int j=0; j<mViews.get(i).size(); j++){
                View child = mViews.get(i).get(j);
                //如果不可见，不设置它的位置
                if(child.getVisibility() == View.GONE){
                    continue;
                }
                //注意这里获得的是父View的LayoutParams
                MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc+ child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);
                /*
                Log.e("布局了", "第" + i +" "+ j+ "个,"+
                        "lc:"+ lc+
                        ",tc:"+ tc+
                        ",rc" + rc+
                        ",bc" + bc);
                */
                left = rc + lp.rightMargin;
            }
            left = paddingTop;
            top += mLineHeight.get(i);
        }
    }

    //与当前ViewGroup对应的LayoutParams为MarginLayoutParams
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    //为FlowLayout设置(刷新)Adapter
    public void setAdapter(ListAdapter adapter){
        //注销监听者
        if(mAdapter != null && mDataSetObserver != null){
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        //ViewGroup中的方法
        removeAllViews();
        mAdapter = adapter;
        //注册监听者
        if(mAdapter != null){
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public ListAdapter getAdapter(){
        return mAdapter;
    }


    //此内部类作为一个Adapter的监听者，在Adapter调用notifyDataChanged时
    //完成View重置的功能
    class AdapterDataSetObserver extends DataSetObserver{
        @Override
        public void onChanged() {
            super.onChanged();
            reloadData();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }
    //重置所有View并且为View注册监听者
    private void reloadData(){
        removeAllViews();

        //重置建每一个子View，添加到FlowLayout中，并设置点击事件监听者
        for(int i=0; i<mAdapter.getCount(); i++){
            final int j = i;
            //View是通过Adapter的getView获得的，
            // 注意最后一个参数传入FlowLayout，这个方法在内部inflate会用到
            final View child = mAdapter.getView(j, null, this);
            //重置点击状态为false
            mCheckedTagArray.put(i, false);
            //把通过Adapter获得的View添加到FlowLayout中
            /*addView(child, new MarginLayoutParams(
                    new LayoutParams(LayoutParams.WRAP_CONTENT
                            , LayoutParams.WRAP_CONTENT)));*/
            //将获得的View添加到FlowLayout中
            addView(child);
            //每个View的监听者其实就是它自己
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //如果这个按钮被点击过,我们改变它的状态
                    if(mCheckedTagArray.get(j)){
                        mCheckedTagArray.put(j, false);
                        child.setSelected(false);
                    }else{//如果这个按钮没被点击过（这里似乎可以不用这么麻烦）
                        //把状态清零
                        for(int k=0; k<mAdapter.getCount(); k++){
                            mCheckedTagArray.put(k, false);
                            getChildAt(k).setSelected(false);
                        }
                        //将该按钮设为已点击
                        mCheckedTagArray.put(j, true);
                        child.setSelected(true);
                    }
                }
            });
        }// end for

    }
}
