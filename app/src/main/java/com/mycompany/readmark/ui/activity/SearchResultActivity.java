package com.mycompany.readmark.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import com.mycompany.readmark.R;
import com.mycompany.readmark.api.presenter.IBookListPresenter;
import com.mycompany.readmark.api.presenter.impl.BookListPresenterImpl;
import com.mycompany.readmark.api.view.IBookListView;
import com.mycompany.readmark.bean.http.BookInfoResponse;
import com.mycompany.readmark.bean.http.BookListResponse;
import com.mycompany.readmark.ui.adapter.BookListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo.
 */

public class SearchResultActivity extends BaseActivity implements IBookListView, SwipeRefreshLayout.OnRefreshListener {
    private static String q;
    private static final String fields = "id,title,subtitle,origin_title,rating,author,translator,publisher,pubdate,summary,images,pages,price,binding,isbn13";
    private static final int count = 20;
    private static int page = 0;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private IBookListPresenter mBookListPresenter;
    private GridLayoutManager mLayoutManager;
    private List<BookInfoResponse> mBookInfoResponses;
    private BookListAdapter mBookListAdapter;

    private boolean isLoadAll = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initEvents() {
        q = getIntent().getStringExtra("q");
        setTitle(q);
        mBookListPresenter = new BookListPresenterImpl(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.recycler_color1, R.color.recycler_color2,
                R.color.recycler_color3, R.color.recycler_color4);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mBookListAdapter.getItemColumnSpan(position);
            }
        });
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mBookInfoResponses = new ArrayList<>();
        mBookListAdapter = new BookListAdapter(this, mBookInfoResponses, 1);
        mRecyclerView.setAdapter(mBookListAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItem;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == mBookInfoResponses.size()){
                    onLoadMore();
                }
            }
        });*/

        //立即加载数据
        onRefresh();
    }

    private void onLoadMore(){
        if(!isLoadAll){

        }
    }


    @Override
    public void showMessage(String msg) {
        if(msg != null){
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showProgress() {
        if(!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

    }

    @Override
    public void hideProgress() {
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    /**
     * 重新刷新
     * @param result
     */
    @Override
    public void refreshData(Object result) {
        if(result instanceof BookListResponse){
            mBookInfoResponses.clear();
            mBookInfoResponses.addAll(((BookListResponse) result).getBooks());
            mBookListAdapter.notifyDataSetChanged();
            page++;
        }
    }

    /**
     * 继续添加
     * @param result
     */
    @Override
    public void addData(Object result) {
        if(result instanceof BookListResponse){
            mBookInfoResponses.addAll(((BookListResponse) result).getBooks());
            mBookListAdapter.notifyDataSetChanged();
            page++;
        }
    }

    @Override
    public void onRefresh() {
        if(!mSwipeRefreshLayout.isRefreshing()){
            mBookListPresenter.loadBooks(q, null, 0, count, fields);
            page = 1;
        }
    }


    @Override
    protected void onDestroy() {
        mBookListPresenter.cancelLoading();
        super.onDestroy();
    }
}
