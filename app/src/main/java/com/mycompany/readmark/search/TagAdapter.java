package com.mycompany.readmark.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycompany.readmark.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/1/11.
 */
public class TagAdapter<T> extends BaseAdapter implements FlowLayout.OnTagMovedListener{
    private final Context mContext;
    private final List<T> mDataList;
    private boolean isDeleteShowed;
    private boolean isTagMoving;
    private int mTempPos;

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
        View view = inflater.inflate(R.layout.textview_tag, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.tag_textview);
        ImageView close = (ImageView) view.findViewById(R.id.tag_close);

        T t = mDataList.get(position);

        close.setVisibility(View.INVISIBLE);
        if(t instanceof String){
            textView.setText((String)t);
        }

        if(position == mTempPos && isTagMoving){
            view.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    public void resetAllDatas(List<T> datas){
        mDataList.clear();
        mDataList.addAll(datas);
    }

    public boolean isDeleteShowed() {
        return isDeleteShowed;
    }

    public void setIsDeleteShowed(boolean isDeleteShowed) {
        this.isDeleteShowed = isDeleteShowed;
    }

    public void onTagMoved(int fromPos, int toPos, boolean isMoving){
        T t = mDataList.get(fromPos);
        mDataList.remove(fromPos);
        mDataList.add(toPos, t);

        isTagMoving = isMoving;
        mTempPos = toPos;

        notifyDataSetChanged();
    }
}
