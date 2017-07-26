package com.mycompany.readmark.ui.adapter.commen;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * RV的包装类，让RV能直接添加header和footer
 */

public class WrapRecyclerView extends RecyclerView {

    private static final String TAG = "WrapRecyclerView";
    private View mEmptyView;

    private View mLoadingView;

    private WrapRecyclerAdapter mWrapAdapter;

    private Adapter mSrcAdapter;

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if(mSrcAdapter == null){
                return;
            }
            if(mWrapAdapter != mSrcAdapter){
                mWrapAdapter.notifyDataSetChanged();
            }
            dataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if(mSrcAdapter == null){
                return;
            }
            if(mWrapAdapter != mSrcAdapter){
                mWrapAdapter.notifyItemChanged(positionStart);
            }
            dataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if(mSrcAdapter == null){
                return;
            }
            if(mWrapAdapter != mSrcAdapter){
                mWrapAdapter.notifyItemChanged(positionStart, payload);
            }
            dataChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if(mSrcAdapter == null){
                return;
            }
            if(mWrapAdapter != mSrcAdapter){
                mWrapAdapter.notifyItemInserted(positionStart);
            }
            dataChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if(mSrcAdapter == null){
                return;
            }
            if(mWrapAdapter != mSrcAdapter){
                mWrapAdapter.notifyItemRemoved(positionStart);
            }
            dataChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if(mSrcAdapter == null){
                return;
            }
            /*if(mWrapAdapter != mSrcAdapter){
                mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
            }*/
            dataChanged();
        }
    };



    public WrapRecyclerView(Context context) {
        this(context, null);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(mSrcAdapter != null){
            mSrcAdapter.unregisterAdapterDataObserver(mDataObserver);
            mSrcAdapter = null;
        }

        mSrcAdapter = adapter;

        if(adapter instanceof WrapRecyclerAdapter){
            mWrapAdapter = (WrapRecyclerAdapter) adapter;
        }else{
            mWrapAdapter = new WrapRecyclerAdapter(adapter);
        }
        //把这个
        super.setAdapter(mWrapAdapter);


        //mWrapAdapter.registerAdapterDataObserver(mDataObserver);
        //给内部的Adapter也注册一个监听者RV
        mSrcAdapter.registerAdapterDataObserver(mDataObserver);
        //RV如果是GridLayoutManager
        mWrapAdapter.adjustSpanSize(this);
    }

    /**
     * 将WrapAdapter的接口移出来
     * @param view
     */
    public void addHeaderView(View view){
        if(mWrapAdapter != null){
            mWrapAdapter.addHeaderView(view);
        }
    }



    public void addFooterView(View view){
        if(mWrapAdapter !=null){
            mWrapAdapter.addFooterView(view);
        }
    }


    public void removeHeaderView(View view){
        if(mWrapAdapter !=null){
            mWrapAdapter.removeHeaderView(view);
        }
    }
    public void removeFooterView(View view){
        if(mWrapAdapter !=null){
            mWrapAdapter.removeFooterView(view);
        }
    }

    public void addEmptyView(View emptyView){
        mEmptyView = emptyView;
        mSrcAdapter.notifyDataSetChanged();
    }

    public void addLoadingView(View loadingView){
        mLoadingView = loadingView;
        mSrcAdapter.notifyDataSetChanged();
    }

    /**
     * 如果有数据，显示数据，没有数据，显示emptyView
     */
    private void dataChanged() {
        if(mSrcAdapter.getItemCount() == 0){
            /*if(mEmptyView != null){
                mEmptyView.setVisibility(VISIBLE);
            }*/
            if(mWrapAdapter.getFooters().size() > 0){
                for(View view : mWrapAdapter.getFooters()){
                    view.setVisibility(INVISIBLE);
                }
            }
            /*if(mWrapAdapter.getHeaders().size() > 0){
                for(View view : mWrapAdapter.getHeaders()){
                    view.setVisibility(INVISIBLE);
                }
            }*/
        }else{
            /*if(mEmptyView != null){
                mEmptyView.setVisibility(INVISIBLE);
            }*/
            if(mWrapAdapter.getFooters().size() > 0){
                for(View view : mWrapAdapter.getFooters()){
                    view.setVisibility(VISIBLE);
                }
            }
            /*if(mWrapAdapter.getHeaders().size() > 0){
                for(View view : mWrapAdapter.getHeaders()){
                    view.setVisibility(VISIBLE);
                }
            }*/
        }
    }



}
