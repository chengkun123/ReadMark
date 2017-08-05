package com.mycompany.readmark.ui.widget.bannerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mycompany.readmark.R;
import com.mycompany.readmark.utils.commen.Blur;


import java.util.List;

import static com.mycompany.readmark.R.id.iv_book_bg;

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
        View view;
        final ImageView bg;
        final ImageView pic;
        if(convertView == null){
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.just_a_banner_image, parent, false);
        }else{
            view = convertView;
        }
        bg = (ImageView) view.findViewById(iv_book_bg);
        pic = (ImageView) view.findViewById(R.id.iv_book_img);
        Glide.with(mContext)
                .load(mDatas.get(position))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        pic.setImageBitmap(resource);
                        bg.setImageBitmap(Blur.apply(resource));
                        bg.setAlpha(0.9f);
                    }
                });

        return view;

        /*Glide.with(this)
                .load(mBookInfoResponse.getImages().getLarge())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv_book_img.setImageBitmap(resource);
                        iv_book_bg.setImageBitmap(Blur.apply(resource));
                        iv_book_bg.setAlpha(0.9f);
                    }
                });*/


        /*ImageView imageView;
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
                .into(imageView);*/





        //return imageView;
    }

    public String getBannerDesc(int position){
        return mDesc.get(position);
    }
}
