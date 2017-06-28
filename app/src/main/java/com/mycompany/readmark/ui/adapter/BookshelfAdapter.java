package com.mycompany.readmark.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.labelview.LabelView;
import com.mycompany.readmark.R;
import com.mycompany.readmark.bean.table.Bookshelf;

import java.util.List;
import java.util.Random;

/**
 * Created by Lenovo.
 */

public class BookshelfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_DEFAULT = 1;
    private final List<Bookshelf> bookshelfs;
    private Context mContext;
    private int columns;

    private boolean isFinished;


    public BookshelfAdapter(List<Bookshelf> bookshelfs, Context context, int columns) {
        this.bookshelfs = bookshelfs;
        mContext = context;
        this.columns = columns;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_DEFAULT){
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_book_shelf, parent, false);
            return new BookShelfHolder(view);
        }else{
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_empty, parent, false);
            return new EmptyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof BookShelfHolder){
            final Bookshelf bookshelf = bookshelfs.get(position);
            Random random = new Random();   //创建随机颜色
            int red = random.nextInt(200) + 22;
            int green = random.nextInt(200) + 22;
            int blue = random.nextInt(200) + 22;
            int color = Color.rgb(red, green, blue);
            ((BookShelfHolder) holder).rl_content.setBackgroundColor(color);
            ((BookShelfHolder) holder).labelView.setText("finished");
            ((BookShelfHolder) holder).tv_bookshelf_name.setText(bookshelf.getTitle());
            ((BookShelfHolder) holder).tv_remark.setText(bookshelf.getRemark());
            ((BookShelfHolder) holder).tv_create_time.setText(bookshelf.getCreateTime());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里显示水波纹进度
                    Toast.makeText(mContext, "显示第"+ position +"的进度", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (bookshelfs.isEmpty()) {
            return 1;
        }
        return bookshelfs.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (bookshelfs == null || bookshelfs.isEmpty()) {
            return TYPE_EMPTY;
        } else {
            return TYPE_DEFAULT;
        }
    }

    public int getItemColumnSpan(int position) {
        switch (getItemViewType(position)) {
            case TYPE_DEFAULT:
                return 1;
            default:
                return columns;
        }
    }


    class BookShelfHolder extends RecyclerView.ViewHolder{
        private RelativeLayout rl_content;
        private LabelView labelView;
        private TextView tv_bookshelf_name;
        private TextView tv_remark;
        private TextView tv_create_time;

        public BookShelfHolder(View itemView){
            super(itemView);
            rl_content = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            labelView = (LabelView) itemView.findViewById(R.id.label_layout);
            tv_bookshelf_name = (TextView) itemView.findViewById(R.id.tv_bookshelf_name);
            tv_remark = (TextView) itemView.findViewById(R.id.tv_remark);
            tv_create_time = (TextView) itemView.findViewById(R.id.tv_create_time);
        }
    }

    class EmptyHolder extends RecyclerView.ViewHolder{
        public EmptyHolder(View itemView){
            super(itemView);
        }
    }
}
