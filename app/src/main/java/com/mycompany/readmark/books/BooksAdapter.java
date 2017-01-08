package com.mycompany.readmark.books;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycompany.readmark.R;

import java.util.List;

/**
 * Created by Lenovo on 2016/11/18.
 */
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

    private List<BooksBean> mBooksBeanList;
    private LayoutInflater mLayoutInflater;


    public BooksAdapter(Context context, List<BooksBean> list){
        mLayoutInflater = LayoutInflater.from(context);
        mBooksBeanList = list;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = mLayoutInflater.inflate(R.layout.recyclerview_item_book, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    public void onBindViewHolder(final MyViewHolder holder, final int position){
        BooksBean book = mBooksBeanList.get(position);
        holder.tvTitle.setText(book.getTitle());
        String desc = "作者: " + (book.getAuthor().size() > 0 ? book.getAuthor().get(0) : "") + "\n副标题: " + book.getSubtitle()
                + "\n出版年: " + book.getPubdate() + "\n页数: " + book.getPages() + "\n定价:" + book.getPrice();
        holder.tvDesc.setText(desc);
        //使用Glide加载网络图片
        Glide.with(holder.ivBook.getContext())
                .load(book.getImage())
                .fitCenter()
                .into(holder.ivBook);
    }

    public int getItemCount(){
        return mBooksBeanList.size();
    }

    public void upDateBooks(List<BooksBean> list){
        mBooksBeanList = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivBook;
        public TextView tvTitle;
        public TextView tvDesc;

        public MyViewHolder(View view){
            super(view);
            ivBook = (ImageView) view.findViewById(R.id.ivBook);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvDesc = (TextView) view.findViewById(R.id.tvDesc);
        }
    }

    public BooksBean getBook(int pos){
        return mBooksBeanList.get(pos);
    }

}
