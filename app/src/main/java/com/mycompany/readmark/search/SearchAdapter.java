package com.mycompany.readmark.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycompany.readmark.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2016/11/22.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>{

    private List<SearchedInfoBean> mDatas;
    private LayoutInflater mLayoutInflater;

    public SearchAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = new ArrayList<>();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mLayoutInflater.inflate(R.layout.recyclerview_item_search, null);
        return new MyViewHolder(view);
    }


    public void onBindViewHolder(final MyViewHolder holder, final int position){
        holder.searchText.setText(mDatas.get(position).getKeyWord());

    }

    public int getItemCount(){
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView searchText;
        public MyViewHolder(View view){
            super(view);
            searchText = (TextView)view.findViewById(R.id.searched_name);
        }
    }

    public void clearDatas(){
        Log.e("执行了", "clearDatas()");
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void update(List<SearchedInfoBean> list){
        Log.e("执行了", "update(List<SearchedInfoBean> list)");
        mDatas = list;
        notifyDataSetChanged();
    }
}

