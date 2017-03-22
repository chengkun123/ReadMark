package com.mycompany.readmark.marker;

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
 * Created by Lenovo on 2017/3/14.
 */
public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.MyViewHolder>{

    private List<MarkerBean> mDates;
    private Context mContext;
    private OnMarkerClickListener mOnImageClickListener;


    public interface OnMarkerClickListener{
        void onMarkerClick(MarkerBean bean);
    }

    public void setOnMarkerClickListener(OnMarkerClickListener onImageClickListener){
        mOnImageClickListener = onImageClickListener;

    }


    public MarkerAdapter(List<MarkerBean> dates, Context context) {
        mDates = dates;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_marker, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(mDates.get(position).getImageUrl())
                .fitCenter()
                .into(holder.image);
        holder.text.setText(mDates.get(position).getMarkerName());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnImageClickListener.onMarkerClick(mDates.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDates.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView text;


        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.marker_image);
            text = (TextView) itemView.findViewById(R.id.marker_name);
        }
    }

    public void upDateMarkers(List<MarkerBean> list){
        mDates = list;
        notifyDataSetChanged();
    }

}
