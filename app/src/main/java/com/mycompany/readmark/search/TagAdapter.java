package com.mycompany.readmark.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycompany.readmark.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/1/11.
 */
public class TagAdapter<T> extends BaseAdapter {
    private final Context mContext;
    private final List<T> mDataList;

    //初始化时并没有添加数据
    public TagAdapter(Context context, List<T> dataList){
        this.mContext = context;
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        TextView textView = (TextView) inflater.inflate(R.layout.textview_tag, parent, false);

        T t = mDataList.get(position);

        if(t instanceof String){
            textView.setText((String)t);
        }

        return textView;
    }

    public void resetAllDatas(List<T> datas){
        mDataList.clear();
        mDataList.addAll(datas);
    }
}
