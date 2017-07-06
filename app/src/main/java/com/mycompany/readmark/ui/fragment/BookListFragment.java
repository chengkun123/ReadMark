package com.mycompany.readmark.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;


import com.mycompany.readmark.R;
import com.mycompany.readmark.api.presenter.IBookListPresenter;
import com.mycompany.readmark.api.presenter.impl.BookListPresenterImpl;
import com.mycompany.readmark.api.view.IBookListView;
import com.mycompany.readmark.bean.http.BookInfoResponse;
import com.mycompany.readmark.bean.http.BookListResponse;
import com.mycompany.readmark.ui.adapter.BookListAdapter;
import com.mycompany.readmark.utils.commen.DensityUtils;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lenovo.
 */

public class BookListFragment extends BaseFragment implements IBookListView, SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private String tag = "hot";
    //对返回信息做过滤,即只返回fields中的内容
    private static final String fields = "id,title,subtitle,origin_title,rating,author,translator,publisher,pubdate,summary,images,pages,price,binding,isbn13,series";
    private static int count = 20;

    private int page = 0;

    private GridLayoutManager mLayoutManager;
    private IBookListPresenter bookListPresenter;
    private BookListAdapter mListAdapter;
    private List<BookInfoResponse> bookInfoResponses;

    public static BookListFragment newInstance(String tag){
        Bundle args = new Bundle();
        args.putString("tag", tag);

        BookListFragment fragment = new BookListFragment();
        fragment.setArguments(args);


        return fragment;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //如果不是recreate的情况
        if (bookInfoResponses == null || bookInfoResponses.size() == 0) {
            page = 0;
        }
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.recycler_content, container, false);
        String s = getArguments().getString("tag");
        if(!TextUtils.isEmpty(s)){
            tag = s;
        }
    }

    @Override
    protected void initEvents() {
        int spanCount = getResources().getInteger(R.integer.home_span_count);
        //创建Presenter并与之建立联系
        bookListPresenter = new BookListPresenterImpl(this);

        bookInfoResponses = new ArrayList<>();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.recycler_color1, R.color.recycler_color2,
                R.color.recycler_color3, R.color.recycler_color4);

        //设置RecyclerView的Manager
        mLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        //???
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mListAdapter.getItemColumnSpan(position);
            }
        });
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //Adapter
        mListAdapter = new BookListAdapter(getActivity(), bookInfoResponses, spanCount);
        mRecyclerView.setAdapter(mListAdapter);
        //动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //监听滚动，主要和加载相关
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollDetector());
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * 并不额外保存自定义的实例变量，交给系统去保存状态
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 如果saveInstanceState是null，表示并不是recreate的情况，系统不会帮助fragment重建状态
     * @param isSavedNull
     */
    @Override
    protected void initData(boolean isSavedNull) {
        if(isSavedNull){
            onRefresh();
        }
    }

    /**
     * 显示消息
     * @param msg
     */
    @Override
    public void showMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示progressbar
     */
    @Override
    public void showProgress() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

    }

    /**
     * 隐藏progressbar
     */
    @Override
    public void hideProgress() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 重置（更新）列表内容
     * @param result
     */
    @Override
    public void refreshData(Object result) {
        if(result instanceof BookListResponse){
            bookInfoResponses.clear();
            bookInfoResponses.addAll(((BookListResponse) result).getBooks());
            mListAdapter.notifyDataSetChanged();
            page++;
        }
    }

    /**
     * 增加列表内容
     * @param result
     */
    @Override
    public void addData(Object result) {
        final int start = bookInfoResponses.size();
        bookInfoResponses.addAll(((BookListResponse)result).getBooks());
        mListAdapter.notifyItemRangeInserted(start, bookInfoResponses.size());
        page++;
    }

    /**
     * 下拉刷新时，从头请求数据
     */
    @Override
    public void onRefresh() {
        bookListPresenter.loadBooks(null, tag, 0, count, fields);
    }


    /**
     * 滑动到最底端时，从当前页继续请求数据
     *
     */
    public void onLoadMore(){
        if(!mSwipeRefreshLayout.isRefreshing()){
            bookListPresenter.loadBooks(null, tag, page * count, count, fields);
        }
    }

    /**
     * 用于监听RecyclerView的滑动
     *
     */
    class RecyclerViewScrollDetector extends RecyclerView.OnScrollListener{
        private int lastVisibleItem;
        private int mScrollThreshold = DensityUtils.dp2px(getActivity(), 1);

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //如果没在滑动 而且 在最底部
            if(newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == mListAdapter.getItemCount()){
                onLoadMore();
            }
        }


        //控制Activity中的FloatingBar的显示与否
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            boolean isObviousDelta = Math.abs(dy) > mScrollThreshold;
            if(isObviousDelta){
                if(dy > 0){

                }else{

                }

            }
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        }
    }
}
