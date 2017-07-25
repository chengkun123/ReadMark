package com.mycompany.readmark.ui.adapter.commen;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycompany.readmark.ui.adapter.BookListAdapter;

/**
 * Created by Lenovo.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    //用 SparseArray代替成员进行缓存，减少findViewById的次数
    private SparseArray<View> mViews;

    public MyViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    /**
     *
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        //找到缓存
        return (T) view;
    }


    public MyViewHolder setText(int viewId, CharSequence text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public MyViewHolder setRating(int viewId, float rating){
        AppCompatRatingBar rb = getView(viewId);
        rb.setRating(rating);
        return this;
    }

    public MyViewHolder setImageResource(int viewId, int resourceId){
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    public MyViewHolder setImagePath(int viewId, HolderImageLoader imageLoader){
        ImageView imageView = getView(viewId);

        /*Glide.with(context)
                .load(path)
                .into((ImageView) getView(viewId));*/
        imageLoader.loadImage(imageView, imageLoader.getPath());

        return this;
    }

    public abstract static class HolderImageLoader{
        private String mPath;
        public HolderImageLoader(String path){
            mPath = path;
        }

        public abstract void loadImage(ImageView imageView, String path);

        public String getPath(){
            return mPath;
        }
    }

}
