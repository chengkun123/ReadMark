package com.mycompany.readmark.ui.widget.bannerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mycompany.readmark.R;


import java.util.List;

/**
 * Created by Lenovo.
 */

public class BannerDataAdapter extends BaseAdapter {
    private List<String> mDatas;
    private List<String> mDesc;
    private Context mContext;

    public BannerDataAdapter(List<String> datas, Context context, List<String> desc) {
        mDatas = datas;
        mDesc = desc;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }else{
            imageView = (ImageView) convertView;
        }

        Glide.with(mContext)
                .load(mDatas.get(position))
                //站位图
                .placeholder(R.drawable.empty)
                .into(imageView);
        return imageView;
    }


    public String getBannerDesc(int position){
        return null;
    }
}
