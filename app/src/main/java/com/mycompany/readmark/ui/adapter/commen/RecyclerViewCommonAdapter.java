package com.mycompany.readmark.ui.adapter.commen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycompany.readmark.R;

import java.util.List;

/**
 * Created by Lenovo.
 */

public abstract class RecyclerViewCommonAdapter<DATA> extends RecyclerView.Adapter<MyViewHolder> {

    private int mLayoutId;

    private List<DATA> mData;

    private LayoutInflater mInflater;

    protected Context mContext;

    private ItemClickListener mItemClickListener;

    private ItemLongClickListener mItemLongClickListener;

    private MultiTypeSupport mMultiTypeSupport;

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public RecyclerViewCommonAdapter(Context context, List<DATA> datas, int layoutId){
        mInflater = LayoutInflater.from(context);
        mData = datas;
        mLayoutId = layoutId;
        mContext = context;
    }

    public RecyclerViewCommonAdapter(Context context, List<DATA> datas, MultiTypeSupport typeSupport){
        this(context, datas, -1);
        mMultiTypeSupport = typeSupport;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*//占位布局
        if(viewType == -1){
            mLayoutId = R.layout.item_empty;
        } else if(mLayoutId == -1 || mMultiTypeSupport != null){
            mLayoutId = viewType;
        }*/

        if(mMultiTypeSupport != null){
            mLayoutId = viewType;
        }

        //普通布局
        View itemView = mInflater.inflate(mLayoutId, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(!mData.isEmpty()){
            bindData(holder, mData.get(position), position);

            if(mItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(position);
                    }
                });
            }

            if(mItemLongClickListener != null){
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return mItemLongClickListener.onItemClick(position);
                    }
                });
            }
        }

    }

    /**
     * 关键参数传出
     * @param holder
     * @param item
     * @param position
     */
    protected abstract void bindData(MyViewHolder holder, DATA item, int position);


    @Override
    public int getItemCount() {
        //占位布局
        /*if(mData.isEmpty()){
            return 1;
        }*/
        return mData.size();
    }

    /**
     * 根据传进来的MultiTypeSupport策略返回布局id
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        //占位布局
        /*if(mData.isEmpty()){
            return -1;
        }*/
        if(mMultiTypeSupport != null){
            return mMultiTypeSupport.getLayoutId(mData.get(position));
        }
        return super.getItemViewType(position);
    }
}
