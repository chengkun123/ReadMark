package com.mycompany.readmark.search;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mycompany.readmark.R;
import com.mycompany.readmark.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/1/11.
 */
public class FlowLayout extends ViewGroup{
    private List<List<View>> mViews = new ArrayList<>();
    private List<Integer> mLineHeight = new ArrayList<>();
    private AdapterDataSetObserver mDataSetObserver;
    private TagAdapter<String> mAdapter;
    private SparseBooleanArray mCheckedTagArray = new SparseBooleanArray();;
    private OnTagLongClickListener mOnTagLongClickListener;
    private OnTagClickListener mOnTagClickListener;

    public interface OnTagLongClickListener {
        void onTagLongClick(boolean isDeleteShowed);
    }

    public interface OnTagClickListener {
        void onTagClick();
    }


    public FlowLayout(Context context) {
        this(context, null);
    }


    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //首先调用View的OnMeasure传入Spec，设置FlowLayout的测量宽高
        //由于AT_MOST模式和EXACTLY模式MeasuredWidth/Height会选取Spec中的值，所以下面还要支持FlowLayou的参数为wrap_content的情况
        //这种情况下MeasuredWidth并不一定会选取Spec中的值（即父View提供的可用的最大值）
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //首先获取可用的最大值
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //最终宽高
        int width = 0;
        int height = 0;

        //行宽高
        int lineWidth = 0;
        int lineHeight = 0;

        int count = getChildCount();

        //为支持FlowLayout的padding属性，在最后返回FlowLayout的测量值时，要加上Padding值
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        //计算最大行宽、总高度
        for(int i=0; i<count; i++){

            View child = getChildAt(i);
            //由于自View是TextView（TextView重写了onMeasure支持自己的wrap_content），测量后可以得到正确的测量值
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();
            //获取TextView的测量值
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //计算添加此child后的行宽。
            int tempWidth = lineWidth +  lp.leftMargin + childWidth + lp.rightMargin;

            //如果需要换行，注意要减去padding
            if (tempWidth > sizeWidth - paddingLeft -paddingRight){
                //更新最终宽高
                width = Math.max(lineWidth, width);
                height += lineHeight;

                //更新行宽高
                lineWidth = childWidth;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;

            }else{//如果不需要换行
                //更新行宽高
                lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
                lineWidth = tempWidth;
                //更新最终宽
                width = Math.max(lineWidth, width);
            }
        } // end for

        //对当前行处理，更新最终宽高
        height += lineHeight;
        width = Math.max(width, lineWidth);

        /*Log.e("控件宽度", "" + sizeWidth);
        Log.e("控件高度", ""+sizeHeight);*/

        //重新设置FlowLayout的MeasuredWidth，
        // 如果是EXACTLY(对应match_parent和具体值)，就设置Spec中的值，
        // 如果是AT_MOST（对应wrap_content），就设置为根据子View布局计算的值width。
        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + paddingLeft + paddingRight,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + paddingTop + paddingBottom);
    }


    //layout方法确定View的位置，onLayout方法确定子View的位置
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
        //int paddingBottom = getPaddingBottom();

        /*
        先确定每行的View
        *
        * */
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


        /*
        * 开始布局子View
        * */
        int left = paddingLeft;
        int top = paddingTop;
        for(int i=0; i<mViews.size(); i++){

            for(int j=0; j<mViews.get(i).size(); j++){
                View child = mViews.get(i).get(j);
                //如果不可见，不设置它的位置
                if(child.getVisibility() == View.GONE){
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc+ child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

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


    //为FlowLayout设置新Adapter
    public void setAdapter(TagAdapter<String> adapter){
        //注销监听者
        if(mAdapter != null && mDataSetObserver != null){
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        removeAllViews();
        mAdapter = adapter;
        //注册监听者
        if(mAdapter != null){
            //在FlowLayout内部创建内部类实现DataSetObserver
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public ListAdapter getAdapter(){
        return mAdapter;
    }


    /*
    * 此内部类作为一个Adapter的监听者，在Adapter调用notifyDataChanged时
    完成View重置的功能
    * */

    class AdapterDataSetObserver extends DataSetObserver{
        @Override
        public void onChanged() {
            super.onChanged();
            reloadViews();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }


    //重置所有View
    private void reloadViews(){
        removeAllViews();

        for(int i=0; i<mAdapter.getCount(); i++){
            final int j = i;

            // 注意最后一个参数传入FlowLayout
            final View child = mAdapter.getView(j, null, this);
            //重置点击状态为false
            mCheckedTagArray.put(i, false);

            addView(child);

            child.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!mAdapter.isDeleteShowed()){
                        mAdapter.setIsDeleteShowed(true);
                        mAdapter.notifyDataSetChanged();
                    }

                    /*
                    * 开始创建Window，把滑动交给FlowLayout自己做
                    * */
                    return true;
                }
            });
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnTagClickListener.onTagClick();
                }
            });
        }// end for

    }

    public void setOnTagLongClickListener(OnTagLongClickListener onTagLongClickListener){
        mOnTagLongClickListener = onTagLongClickListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener){
        mOnTagClickListener = onTagClickListener;
    }
}
