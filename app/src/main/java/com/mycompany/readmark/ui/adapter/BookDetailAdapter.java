package com.mycompany.readmark.ui.adapter;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hymane.expandtextview.ExpandTextView;
import com.mycompany.readmark.R;
import com.mycompany.readmark.bean.http.BookInfoResponse;
import com.mycompany.readmark.bean.http.BookReviewResponse;
import com.mycompany.readmark.bean.http.BookReviewsListResponse;
import com.mycompany.readmark.bean.http.BookSeriesListResponse;
import com.mycompany.readmark.holder.BookSeriesCeilHolder;
import com.mycompany.readmark.ui.activity.BaseActivity;
import com.mycompany.readmark.utils.commen.UIUtils;

import java.util.List;

/**
 * Created by Lenovo.
 */

public class BookDetailAdapter extends RecyclerView.Adapter {
    private static final int TYPE_BOOK_INFO = 0;
    private static final int TYPE_BOOK_BRIEF = 1;
    private static final int TYPE_BOOK_COMMENT = 2;
    private static final int TYPE_BOOK_RECOMMEND = 3;

    public static final int HEADER_COUNT = 2;
    private static final int AVATAR_SIZE_DP = 24;
    private static final int ANIMATION_DURATION = 600;
    //模拟加载时间
    private static final int PROGRESS_DELAY_MIN_TIME = 500;
    private static final int PROGRESS_DELAY_SIZE_TIME = 1000;

    private final BookInfoResponse mBookInfo;
    private final BookReviewsListResponse mReviewsListResponse;
    private final BookSeriesListResponse mSeriesListResponse;

    private boolean isOpen;

    public BookDetailAdapter(BookInfoResponse bookInfo, BookReviewsListResponse reviewsListResponse, BookSeriesListResponse seriesListResponse) {
        mBookInfo = bookInfo;
        mReviewsListResponse = reviewsListResponse;
        mSeriesListResponse = seriesListResponse;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_BOOK_INFO;
        } else if (position == 1) {
            return TYPE_BOOK_BRIEF;
        } else if (position > 1
                && position <
                (mReviewsListResponse == null ?
                        HEADER_COUNT : (mReviewsListResponse.getReviews().size() + HEADER_COUNT))) {
            return TYPE_BOOK_COMMENT;
        } else {
            return TYPE_BOOK_RECOMMEND;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_BOOK_INFO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_info, parent, false);
            return new BookInfoHolder(view);
        } else if (viewType == TYPE_BOOK_BRIEF) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_brief, parent, false);
            return new BookBriefHolder(view);
        } else if (viewType == TYPE_BOOK_COMMENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_comment, parent, false);
            return new BookCommentHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_series, parent, false);
            return new BookSeriesHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BookInfoHolder){
            //10星->5星
            ((BookInfoHolder) holder).ratingBar_hots.setRating(Float.valueOf(mBookInfo.getRating().getAverage()) / 2);
            ((BookInfoHolder) holder).tv_hots_num.setText(mBookInfo.getRating().getAverage());
            ((BookInfoHolder) holder).tv_comment_num.setText(mBookInfo.getRating().getNumRaters() + UIUtils.getContext().getString(R.string.comment_num));
            ((BookInfoHolder) holder).tv_book_info.setText(mBookInfo.getInfoString());

            //通过DiaLog展开的详细信息内容
            final StringBuilder sb = new StringBuilder();
            if(mBookInfo.getAuthor().length > 0){
                sb.append("作者：").append(mBookInfo.getAuthor()[0]).append("\n");
            }
            sb.append("出版社：").append(mBookInfo.getPublisher()).append("\n");
            if(mBookInfo.getSubtitle().isEmpty()){
                ((BookInfoHolder) holder).tv_subtitle.setVisibility(View.GONE);
            }
            sb.append("副标题:").append(mBookInfo.getSubtitle()).append("\n");
            if (mBookInfo.getOrigin_title().isEmpty()) {
                ((BookInfoHolder) holder).tv_origin_title.setVisibility(View.GONE);
            }
            sb.append("原作名:").append(mBookInfo.getOrigin_title()).append("\n");
            if (mBookInfo.getTranslator().length > 0) {
//                ((BookInfoHolder) holder).tv_translator.setText("译者:" + mBookInfo.getTranslator()[0]);
                sb.append("译者:").append(mBookInfo.getTranslator()[0]).append("\n");
            } else {
                ((BookInfoHolder) holder).tv_translator.setVisibility(View.GONE);
            }
            sb.append("出版年:").append(mBookInfo.getPubdate()).append("\n");
            sb.append("页数:").append(mBookInfo.getPages()).append("\n");
            sb.append("定价:").append(mBookInfo.getPrice()).append("\n");
            sb.append("装帧:").append(mBookInfo.getBinding()).append("\n");
            sb.append("isbn:").append(mBookInfo.getIsbn13()).append("\n");
            ((BookInfoHolder) holder).rl_more_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //详细信息已打开
                    if(isOpen){
                        ObjectAnimator.ofFloat(((BookInfoHolder) holder).iv_more_info, "rotation", 90, 0).start();
                        isOpen = false;
                    }else{
                        ObjectAnimator.ofFloat(((BookInfoHolder) holder).iv_more_info, "rotation", 0, 90).start();
                        new AlertDialog.Builder(BaseActivity.activity)
                                .setTitle("详细信息：")
                                .setMessage(sb)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        ObjectAnimator.ofFloat(((BookInfoHolder) holder).iv_more_info, "rotation", 90, 0).start();
                                        isOpen = false;
                                    }
                                })
                                .create()
                                .show();
                        isOpen = true;
                    }

                }
            });
        }else if(holder instanceof BookBriefHolder){
            if(!mBookInfo.getSummary().isEmpty()){
                ((BookBriefHolder) holder).etv_brief.setContent(mBookInfo.getSummary());
            }else{
                ((BookBriefHolder) holder).etv_brief.setContent(UIUtils.getContext().getString(R.string.no_brief));
            }
        }else if(holder instanceof BookCommentHolder){
            List<BookReviewResponse> reviews = mReviewsListResponse.getReviews();
            if(reviews.isEmpty()){
                holder.itemView.setVisibility(View.GONE);
            }
            //通过position判断设置item中的view的可见性
            else if(position == HEADER_COUNT){
                ((BookCommentHolder) holder).tv_comment_title.setVisibility(View.VISIBLE);
            }else if(position == reviews.size() + 1){
                ((BookCommentHolder) holder).tv_more_comment.setVisibility(View.VISIBLE);
                ((BookCommentHolder) holder).tv_more_comment.setText(UIUtils.getContext().getString(R.string.more_brief) + mReviewsListResponse.getTotal() + "条");
                ((BookCommentHolder) holder).tv_more_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            if(position >= 2){

            }
            Glide.with(UIUtils.getContext())
                    .load(reviews.get(position - HEADER_COUNT).getAuthor().getAvatar())
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(((BookCommentHolder) holder).iv_avatar) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(UIUtils.getContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            ((BookCommentHolder) holder).iv_avatar.setImageDrawable(circularBitmapDrawable);
                        }
                    });

            ((BookCommentHolder) holder).tv_user_name.setText(reviews.get(position - HEADER_COUNT).getAuthor().getName());
            if (reviews.get(position - HEADER_COUNT).getRating() != null) {
                ((BookCommentHolder) holder).ratingBar_hots.setRating(Float.valueOf(reviews.get(position - HEADER_COUNT).getRating().getValue()));
            }
            ((BookCommentHolder) holder).tv_comment_content.setText(reviews.get(position - HEADER_COUNT).getSummary());
            ((BookCommentHolder) holder).tv_favorite_num.setText(reviews.get(position - HEADER_COUNT).getVotes() + "");
            ((BookCommentHolder) holder).tv_update_time.setText(reviews.get(position - HEADER_COUNT).getUpdated().split(" ")[0]);
        }else if(holder instanceof BookSeriesHolder){
            final List<BookInfoResponse> seriesBooks = mSeriesListResponse.getBooks();
            if(seriesBooks.isEmpty()){
                holder.itemView.setVisibility(View.GONE);
            }else{
                BookSeriesCeilHolder ceilHolder;
                ((BookSeriesHolder) holder).ll_series_content.removeAllViews();
                for(int i=0; i<seriesBooks.size(); i++){
                    ceilHolder = new BookSeriesCeilHolder(holder.itemView.getContext(), seriesBooks.get(i));
                    ((BookSeriesHolder) holder).ll_series_content.addView(ceilHolder.getContentView());
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        int count = HEADER_COUNT;
        if(mReviewsListResponse != null){
            count += mReviewsListResponse.getReviews().size();
        }
        if(mSeriesListResponse != null && !mSeriesListResponse.getBooks().isEmpty()){
            count += 1;
        }
        return count;
    }

    class BookInfoHolder extends RecyclerView.ViewHolder{
        private AppCompatRatingBar ratingBar_hots;
        private TextView tv_hots_num;
        private TextView tv_comment_num;
        private TextView tv_book_info;
        private ImageView iv_more_info;
        private RelativeLayout rl_more_info;

        private TextView tv_subtitle;
        private TextView tv_origin_title;
        private TextView tv_translator;

        public BookInfoHolder(View itemView) {
            super(itemView);
            ratingBar_hots = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBar_hots);
            tv_hots_num = (TextView) itemView.findViewById(R.id.tv_hots_num);
            tv_comment_num = (TextView) itemView.findViewById(R.id.tv_comment_num);
            tv_book_info = (TextView) itemView.findViewById(R.id.tv_book_info);
            iv_more_info = (ImageView) itemView.findViewById(R.id.iv_more_info);
            rl_more_info = (RelativeLayout) itemView.findViewById(R.id.rl_more_info);

            tv_subtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            tv_origin_title = (TextView) itemView.findViewById(R.id.tv_origin_title);
            tv_translator = (TextView) itemView.findViewById(R.id.tv_translator);

        }
    }

    class BookBriefHolder extends RecyclerView.ViewHolder{
        private ExpandTextView etv_brief;

        public BookBriefHolder(View itemView) {
            super(itemView);
            etv_brief = (ExpandTextView) itemView.findViewById(R.id.etv_brief);
        }
    }

    class BookCommentHolder extends RecyclerView.ViewHolder{
        private TextView tv_comment_title;
        private ImageView iv_avatar;
        private TextView tv_user_name;
        private AppCompatRatingBar ratingBar_hots;
        private TextView tv_comment_content;
        private ImageView iv_favorite;
        private TextView tv_favorite_num;
        private TextView tv_update_time;
        private TextView tv_more_comment;
        public BookCommentHolder(View itemView) {
            super(itemView);
            tv_comment_title = (TextView) itemView.findViewById(R.id.tv_comment_title);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            ratingBar_hots = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBar_hots);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            iv_favorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
            tv_favorite_num = (TextView) itemView.findViewById(R.id.tv_favorite_num);
            tv_update_time = (TextView) itemView.findViewById(R.id.tv_update_time);
            tv_more_comment = (TextView) itemView.findViewById(R.id.tv_more_comment);
        }
    }

    class  BookSeriesHolder extends RecyclerView.ViewHolder{
        private HorizontalScrollView hsv_series;
        private LinearLayout ll_series_content;

        public BookSeriesHolder(View itemView) {
            super(itemView);
            hsv_series = (HorizontalScrollView) itemView.findViewById(R.id.hsv_series);
            ll_series_content = (LinearLayout) itemView.findViewById(R.id.ll_series_content);
        }

    }


}
