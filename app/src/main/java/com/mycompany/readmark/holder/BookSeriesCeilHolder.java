package com.mycompany.readmark.holder;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycompany.readmark.R;
import com.mycompany.readmark.bean.http.BookInfoResponse;
import com.mycompany.readmark.utils.commen.UIUtils;

/**
 * Created by Lenovo.
 */

public class BookSeriesCeilHolder {
    private BookInfoResponse mBookInfoResponse;
    private View mContentView;
    private ImageView iv_book_img;
    private TextView tv_title;
    private AppCompatRatingBar ratingBar_hots;
    private TextView tv_hots_num;
    private Context mContext;
    public BookSeriesCeilHolder(Context context, BookInfoResponse response){
        mContext = context;
        mBookInfoResponse = response;
        initView();
        initEvent();
    }

    private void initView(){
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.item_book_series_ceil, null, false);
        iv_book_img = (ImageView) mContentView.findViewById(R.id.iv_book_img);
        tv_title = (TextView) mContentView.findViewById(R.id.tv_title);
        ratingBar_hots = (AppCompatRatingBar) mContentView.findViewById(R.id.ratingBar_hots);
        tv_hots_num = (TextView) mContentView.findViewById(R.id.tv_hots_num);
    }

    private void initEvent(){
        Glide.with(UIUtils.getContext())
                .load(mBookInfoResponse.getImages().getLarge())
                .into(iv_book_img);
        tv_title.setText(mBookInfoResponse.getTitle());
        ratingBar_hots.setRating(Float.valueOf(mBookInfoResponse.getRating().getAverage()) / 2);
        tv_hots_num.setText(mBookInfoResponse.getRating().getAverage());
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public View getContentView() {
        return mContentView;
    }


}
