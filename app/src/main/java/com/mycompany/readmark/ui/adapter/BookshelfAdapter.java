package com.mycompany.readmark.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.labelview.LabelView;
import com.mycompany.readmark.R;
import com.mycompany.readmark.bean.table.Bookshelf;
import com.mycompany.readmark.holder.WaveLoadingViewHolder;

import java.util.List;

/**
 * Created by Lenovo.
 */

public class BookshelfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_DEFAULT = 1;
    private final List<Bookshelf> bookshelfs;
    private Context mContext;
    private int columns;
    private OnAdjustmentConfirmListener mOnAdjustmentConfirmListener;
    private OnDeleteConfirmListener mOnDeleteConfirmListener;


    //private boolean isFinished;
    private boolean isSorting;

    public void setOnAdjustmentConfirmListener(OnAdjustmentConfirmListener listener){
        mOnAdjustmentConfirmListener = listener;
    }

    public void setOnDeleteConfirmListener(OnDeleteConfirmListener listener){
        mOnDeleteConfirmListener = listener;
    }

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

            /*//这里改变一下
            Random random = new Random();   //创建随机颜色
            int red = random.nextInt(200) + 22;
            int green = random.nextInt(200) + 22;
            int blue = random.nextInt(200) + 22;
            int color = Color.rgb(red, green, blue);*/

            ((BookShelfHolder) holder).rl_content.setBackgroundColor(bookshelf.getColor());
            if(bookshelf.getFinished() == 0){
                ((BookShelfHolder) holder).labelView.setVisibility(View.INVISIBLE);
            }else{
                ((BookShelfHolder) holder).labelView.setVisibility(View.VISIBLE);
                ((BookShelfHolder) holder).labelView.setText("FINISHED");
            }
            ((BookShelfHolder) holder).tv_bookshelf_name.setText(bookshelf.getTitle());
            ((BookShelfHolder) holder).tv_remark.setText(bookshelf.getRemark());
            ((BookShelfHolder) holder).tv_create_time.setText(bookshelf.getCreateTime());
            if(!isSorting){
                holder.itemView.setAlpha(1.0f);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //这里显示水波纹进度
                        //Toast.makeText(mContext, "显示第"+ position +"的进度", Toast.LENGTH_SHORT).show();
                        showWaveLoadingView(mContext, bookshelf);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDeleteDialog(mContext, bookshelf);
                        return true;
                    }
                });
            }else{
                holder.itemView.setAlpha(0.4f);
                holder.itemView.setOnClickListener(null);
            }

        }
    }

    /*
    * 删除书签
    * */
    private void showDeleteDialog(Context context, final Bookshelf bookshelf){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false)
                .setTitle("确定删除书签？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnDeleteConfirmListener.onDeleteConfirm(bookshelf);
                    }
                })
                .create()
                .show();
    }

    /*
    * 进度管理
    * */
    private void showWaveLoadingView(Context context, final Bookshelf bookshelf){
        final WaveLoadingViewHolder waveLoadingViewHolder = new WaveLoadingViewHolder(context, bookshelf);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //final int space = DensityUtils.dp2px(UIUtils.getContext(), 16);

        builder.setCancelable(false)
                .setView(waveLoadingViewHolder.getContentView())
                .setTitle("阅读进度")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(waveLoadingViewHolder.isDateQualified()){
                            mOnAdjustmentConfirmListener.onAdjustmentConfirm(bookshelf, true);
                        }else{
                            mOnAdjustmentConfirmListener.onAdjustmentConfirm(bookshelf, false);
                        }
                    }
                })
                .create()
                .show();

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



    public boolean isSorting() {
        return isSorting;
    }

    public void setSorting(boolean sorting) {
        isSorting = sorting;
    }


    public interface OnAdjustmentConfirmListener{
        void onAdjustmentConfirm(Bookshelf bookshelf, boolean succeed);
    }

    public interface OnDeleteConfirmListener{
        void onDeleteConfirm(Bookshelf bookshelf);
    }
}
