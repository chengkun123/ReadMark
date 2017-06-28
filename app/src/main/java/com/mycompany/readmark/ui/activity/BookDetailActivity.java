package com.mycompany.readmark.ui.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mycompany.readmark.R;
import com.mycompany.readmark.api.presenter.impl.BookDetailPresenterImpl;
import com.mycompany.readmark.api.presenter.impl.BookshelfPresenterImpl;
import com.mycompany.readmark.api.view.IBookDetailView;
import com.mycompany.readmark.api.view.IBookListViewAdapter;
import com.mycompany.readmark.bean.http.BookInfoResponse;
import com.mycompany.readmark.bean.http.BookReviewsListResponse;
import com.mycompany.readmark.bean.http.BookSeriesListResponse;
import com.mycompany.readmark.ui.adapter.BookDetailAdapter;
import com.mycompany.readmark.utils.commen.Blur;
import com.mycompany.readmark.utils.commen.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo.
 */

public class BookDetailActivity extends BaseActivity implements IBookDetailView {

    private static final String COMMENT_FIELDS = "id,rating,author,title,updated,comments,summary,votes,useless";
    private static final String SERIES_FIELDS = "id,title,subtitle,origin_title,rating,author,translator,publisher,pubdate,summary,images,pages,price,binding,isbn13,series";
    private static final int REVIEWS_COUNT = 5;
    private static final int SERIES_COUNT = 6;
    private static final int PAGE = 0;


    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private BookDetailAdapter mDetailAdapter;
    private ImageView iv_book_img;
    private ImageView iv_book_bg;

    //详细信息
    private BookInfoResponse mBookInfoResponse;
    //评论列表
    private BookReviewsListResponse mReviewsListResponse;
    //系列丛书列表
    private BookSeriesListResponse mSeriesListResponse;
    //获取详细信息的model
    private BookDetailPresenterImpl mBookDetailPresenter;

    //仅用来进行存储
    private BookshelfPresenterImpl mBookshelfPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("onCreate", "调用了");
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        mToolbar.setNavigationIcon(AppCompatResources.getDrawable(this, R.drawable.ic_action_clear));
    }

    @Override
    protected void initEvents() {
        Log.e("initevent", "调用了");
        mBookDetailPresenter = new BookDetailPresenterImpl(this);
        //为了初始化Adapter
        mReviewsListResponse = new BookReviewsListResponse();
        mSeriesListResponse = new BookSeriesListResponse();

        mBookInfoResponse = (BookInfoResponse) getIntent().getSerializableExtra("book_info");

        mLayoutManager = new LinearLayoutManager(BookDetailActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDetailAdapter = new BookDetailAdapter(mBookInfoResponse, mReviewsListResponse, mSeriesListResponse);
        mRecyclerView.setAdapter(mDetailAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        iv_book_img = (ImageView) findViewById(R.id.iv_book_img);
        iv_book_bg = (ImageView) findViewById(R.id.iv_book_bg);
        mCollapsingLayout.setTitle(mBookInfoResponse.getTitle());

        Bitmap bitmap = getIntent().getParcelableExtra("book_img");
        if(bitmap != null){
            iv_book_img.setImageBitmap(bitmap);
            iv_book_bg.setImageBitmap(Blur.apply(bitmap));
            iv_book_bg.setAlpha(0.9f);
        }else{
            Glide.with(this)
                    .load(mBookInfoResponse.getImages().getLarge())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            iv_book_img.setImageBitmap(resource);
                            iv_book_bg.setImageBitmap(Blur.apply(resource));
                            iv_book_bg.setAlpha(0.9f);
                        }
                    });
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookDetailActivity.this);
                builder.setCancelable(false)
                        .setTitle("确定添加书签？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mBookshelfPresenter = new BookshelfPresenterImpl(new IBookListViewAdapter() {
                                    @Override
                                    public void showMessage(String msg) {
                                        super.showMessage(msg);
                                        Toast.makeText(BookDetailActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void showProgress() {
                                        super.showProgress();
                                    }
                                });
                                mBookshelfPresenter.addBookshelf(mBookInfoResponse.getTitle(), "", System.currentTimeMillis()+"");
                            }
                        })
                        .create().show();
            }
        });

        Log.e("书的id是：", mBookInfoResponse.getId()+"as");
        //加载评论
        mBookDetailPresenter.loadReviews(mBookInfoResponse.getId(), PAGE * REVIEWS_COUNT, REVIEWS_COUNT, COMMENT_FIELDS);

    }

    @Override
    public void showMessage(String msg) {
        Snackbar.make(mToolbar, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void updateView(Object result) {
        if(result instanceof BookReviewsListResponse){
            final BookReviewsListResponse response = (BookReviewsListResponse) result;
            //为ReviewsResponse赋值
            mReviewsListResponse.setTotal(response.getTotal());
            mReviewsListResponse.getReviews().addAll(response.getReviews());
            mDetailAdapter.notifyDataSetChanged();
            if(mBookInfoResponse.getSeries() != null){
                mBookDetailPresenter.loadSeries(mBookInfoResponse.getSeries().getId(), PAGE * SERIES_COUNT, 6, SERIES_FIELDS);
            }
        }else if(result instanceof BookSeriesListResponse){
            final BookSeriesListResponse response = (BookSeriesListResponse) result;
            mSeriesListResponse.setTotal(response.getTotal());
            mSeriesListResponse.getBooks().addAll(response.getBooks());
            mDetailAdapter.notifyDataSetChanged();
        }
    }
}
