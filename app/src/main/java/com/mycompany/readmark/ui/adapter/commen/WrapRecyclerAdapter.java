package com.mycompany.readmark.ui.adapter.commen;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter的包装类，让RV能添加header和footer但是不为它们绑定数据
 */

public class WrapRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.Adapter mAdapter;

    private SparseArray<View> mHeaders, mFooters;

    private List<View> mHeadersList, mFootersList;


    private static int BASE_HEADER_KEY = 1000000;
    private static int BASE_FOOTER_KEY = 2000000;

    public WrapRecyclerAdapter(RecyclerView.Adapter adapter){
        mAdapter = adapter;
        mHeaders = new SparseArray<>();
        mFooters = new SparseArray<>();
        mHeadersList = new ArrayList<>();
        mFootersList = new ArrayList<>();
    }


    /**
     * 头和脚不需要绑定绑定数据
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //如果是头部和底部，不存在type，直接新建一个ViewHolder
        if(isHeaderViewType(viewType)){
            return createHeaderFooterViewHolder(mHeaders.get(viewType));
        }else if(isFooterViewType(viewType)){
            return createHeaderFooterViewHolder(mFooters.get(viewType));
        }

        //列表

        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    /**
     * key是否存在有效索引
     * @param viewType
     * @return
     */
    private boolean isFooterViewType(int viewType) {
        int position = mFooters.indexOfKey(viewType);
        return position >= 0;
    }

    private boolean isHeaderViewType(int viewType) {
        int position = mHeaders.indexOfKey(viewType);
        return position >= 0;
    }

    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view) {

        };
    }


    /**
     * 头和脚并不绑定数据，只有中间才绑定
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderPosition(position) || isFooterPosition(position)) {
            return;
        }

        position = position - mHeaders.size();
        mAdapter.onBindViewHolder(holder, position);
    }

    /**
     * 判断position是否属于footer的位置
     * @param position
     * @return
     */
    private boolean isFooterPosition(int position) {
        if(position >= (mHeaders.size() + mAdapter.getItemCount())){
            return true;
        }
        return false;
    }

    /**
     * 判断position是否属于header的位置
     * @param position
     * @return
     */
    private boolean isHeaderPosition(int position) {
        if(position < mHeaders.size()){
            return true;
        }
        return false;
    }

    /**
     * 个数是三者相加
     * @return
     */
    @Override
    public int getItemCount() {
        return mHeaders.size() + mAdapter.getItemCount() + mFooters.size();
    }

    /**
     * 头和脚都返回key, 中间的直接返回原Adapter的type
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if(isHeaderPosition(position)){
            //返回key作为"type"
            return mHeaders.keyAt(position);
        }
        if(isFooterPosition(position)){
            position = position - mHeaders.size() - mAdapter.getItemCount();
            return mFooters.keyAt(position);
        }

        position = position - mHeaders.size();
        return mAdapter.getItemViewType(position);
    }

    /**
     * 把view添加在map中
     * @param view
     */
    public void addHeaderView(View view){
        if(mHeaders.indexOfValue(view) == -1){
            mHeaders.put(BASE_HEADER_KEY++, view);
            mHeadersList.add(view);
        }
        mAdapter.notifyDataSetChanged();
    }


    public void addFooterView(View view){
        if(mFooters.indexOfValue(view) == -1){
            mFooters.put(BASE_FOOTER_KEY++, view);
            mFootersList.add(view);
        }
        mAdapter.notifyDataSetChanged();
    }


    public void removeHeaderView(View view){
        if(mHeaders.indexOfValue(view) >= 0){
            mHeaders.removeAt(mHeaders.indexOfValue(view));
            mHeadersList.remove(view);
            mAdapter.notifyDataSetChanged();
        }
    }
    public void removeFooterView(View view){
        if(mFooters.indexOfValue(view) >= 0){
            mFooters.removeAt(mFooters.indexOfValue(view));
            mFootersList.remove(view);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 解决GridLayoutManager头和脚不占一行的问题
     * @param recycler
     */
    public void adjustSpanSize(RecyclerView recycler) {
        if (recycler.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter =
                            isHeaderPosition(position) || isFooterPosition(position);
                    return isHeaderOrFooter ? layoutManager.getSpanCount() : 1;
                }
            });
        }
    }


    public List<View> getHeaders(){
        return mHeadersList;
    }

    public List<View> getFooters(){
        return mFootersList;
    }


}
