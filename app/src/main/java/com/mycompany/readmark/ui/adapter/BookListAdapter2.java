package com.mycompany.readmark.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycompany.readmark.R;
import com.mycompany.readmark.bean.http.BookInfoResponse;
import com.mycompany.readmark.ui.adapter.BookListAdapter;
import com.mycompany.readmark.ui.adapter.commen.MultiTypeSupport;
import com.mycompany.readmark.ui.adapter.commen.MyViewHolder;
import com.mycompany.readmark.ui.adapter.commen.RecyclerViewCommonAdapter;

import java.util.List;

/**
 * Created by Lenovo.
 */

public class BookListAdapter2 extends RecyclerViewCommonAdapter<BookInfoResponse> {

    public BookListAdapter2(Context context, List list, int layoutId) {
        super(context, list, layoutId);
    }

    public BookListAdapter2(Context context, List datas, MultiTypeSupport typeSupport){
        super(context, datas, typeSupport);
    }

    @Override
    protected void bindData(MyViewHolder holder, final BookInfoResponse item, int position) {
        holder.setText(R.id.tv_book_title, item.getTitle())
                .setText(R.id.tv_hots_num, item.getRating().getAverage())
                .setText(R.id.tv_book_info, item.getInfoString())
                .setText(R.id.tv_book_description, "\u3000" + item.getSummary())
                .setRating(R.id.ratingBar_hots, Float.valueOf(item.getRating().getAverage()) / 2)
                .setImagePath(R.id.iv_book_img, new MyViewHolder.HolderImageLoader(item.getImages().getLarge()) {
                    @Override
                    public void loadImage(ImageView imageView, String path) {
                        Glide.with(mContext)
                                .load(item.getImages().getLarge())
                                .into(imageView);
                    }
                });
    }
}
