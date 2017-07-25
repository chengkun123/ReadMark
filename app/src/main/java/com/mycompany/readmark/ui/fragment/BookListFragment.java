package com.mycompany.readmark.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.mycompany.readmark.R;
import com.mycompany.readmark.api.presenter.IBookListPresenter;
import com.mycompany.readmark.api.presenter.impl.BookListPresenterImpl;
import com.mycompany.readmark.api.view.IBookListView;
import com.mycompany.readmark.bean.http.BookInfoResponse;
import com.mycompany.readmark.bean.http.BookListResponse;
import com.mycompany.readmark.ui.activity.BookDetailActivity;
import com.mycompany.readmark.ui.adapter.BookListAdapter;
import com.mycompany.readmark.ui.adapter.BookListAdapter2;
import com.mycompany.readmark.ui.adapter.commen.DefaultLoadRefreshCreator;
import com.mycompany.readmark.ui.adapter.commen.DefaultRefreshCreator;
import com.mycompany.readmark.ui.adapter.commen.ItemClickListener;
import com.mycompany.readmark.ui.adapter.commen.LoadRefreshRecyclerView;
import com.mycompany.readmark.ui.adapter.commen.MultiTypeSupport;
import com.mycompany.readmark.ui.adapter.commen.RefreshRecyclerView;
import com.mycompany.readmark.ui.adapter.commen.WrapRecyclerAdapter;
import com.mycompany.readmark.ui.adapter.commen.WrapRecyclerView;
import com.mycompany.readmark.ui.widget.bannerview.BannerDataAdapter;

import com.mycompany.readmark.ui.widget.bannerview.BannerView;
import com.mycompany.readmark.utils.commen.DensityUtils;
import com.mycompany.readmark.utils.commen.UIUtils;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lenovo.
 */

public class BookListFragment extends BaseFragment
        implements IBookListView, RefreshRecyclerView.OnRefreshListener, LoadRefreshRecyclerView.OnLoadMoreListener {

    @BindView(R.id.recyclerView)
    LoadRefreshRecyclerView mRecyclerView;
    /*@BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;*/

    private String tag = "hot";
    //对返回信息做过滤,即只返回fields中的内容
    private static final String fields = "id,title,subtitle,origin_title,rating,author,translator,publisher,pubdate,summary,images,pages,price,binding,isbn13,series";
    private static int count = 20;

    private int page = 0;

    private GridLayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManager2;

    private IBookListPresenter bookListPresenter;
    private BookListAdapter mListAdapter;
    private BookListAdapter2 mListAdapter2;
    private WrapRecyclerAdapter mAdapter;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //((MainActivity)getActivity()).setThemeSetter(mRootView);
    }

    @Override
    public void onDestroyView() {
        //在这里回调传回mRootView
        if(mRootView != null){
            //((MainActivity)getActivity()).clearThemeSetter(mRootView);
        }
        super.onDestroyView();
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
        /*mSwipeRefreshLayout.setColorSchemeResources(R.color.recycler_color1, R.color.recycler_color2,
                R.color.recycler_color3, R.color.recycler_color4);*/

        /*//设置RecyclerView的Manager
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
        mRecyclerView.setAdapter(mListAdapter);*/

        /*
        * 新增
        * */
        mLayoutManager2 = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager2);
        //不支持多布局时
        //mListAdapter2 = new BookListAdapter2(getActivity(), bookInfoResponses, R.layout.item_book_list);


        //支持多布局时,传入根据item（一般有标记）返回不同布局id的策略（接口）
        mListAdapter2 = new BookListAdapter2(getActivity()
                , bookInfoResponses
                , new MultiTypeSupport<BookInfoResponse>() {
            @Override
            public int getLayoutId(BookInfoResponse item) {
                //这里一般根据BookInfoResponse去返回。
                return R.layout.item_book_list;
            }
        });
        /*//包装支持头部和脚部
        mAdapter = new WrapRecyclerAdapter(mListAdapter2);*/
        mRecyclerView.setAdapter(mListAdapter2);

        //刷新头部
        DefaultRefreshCreator refreshCreator = new DefaultRefreshCreator();
        mRecyclerView.addRefreshViewCreator(refreshCreator);
        mRecyclerView.setOnRefreshListener(this);
        //普通头部
        BannerView banner = (BannerView) LayoutInflater.from(getActivity())
                .inflate(R.layout.just_a_banner_view, mRecyclerView, false);
        List<String> bannerDatas = new ArrayList<>();
        bannerDatas.add("https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s160-c/A%252520Photographer.jpg");
        bannerDatas.add("https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s160-c/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg");
        bannerDatas.add("https://lh5.googleusercontent.com/-7qZeDtRKFKc/URquWZT1gOI/AAAAAAAAAbs/hqWgteyNXsg/s160-c/Another%252520Rockaway%252520Sunset.jpg");
        bannerDatas.add("https://lh3.googleusercontent.com/--L0Km39l5J8/URquXHGcdNI/AAAAAAAAAbs/3ZrSJNrSomQ/s160-c/Antelope%252520Butte.jpg");
        BannerDataAdapter bannerDataAdapter = new BannerDataAdapter(bannerDatas, getActivity(), null);
        banner.setAdapter(bannerDataAdapter);
        banner.startRoll();
        mRecyclerView.addHeaderView(banner);
        /*//空白
        View emptyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.item_empty, mRecyclerView, false);
        mRecyclerView.addEmptyView(emptyView);*/

        //刷新的尾部
        DefaultLoadRefreshCreator defaultLoadRefreshCreator = new DefaultLoadRefreshCreator();
        mRecyclerView.addLoadViewCreator(defaultLoadRefreshCreator);
        mRecyclerView.setOnLoadMoreListener(this);

        //点击事件传出
        mListAdapter2.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle b = new Bundle();
                b.putSerializable("book_info", bookInfoResponses.get(position));
                Intent intent = new Intent(UIUtils.getContext(), BookDetailActivity.class);
                intent.putExtras(b);
                UIUtils.startActivity(intent);
            }
        });

        //动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        //添加更多直接在RV内部处理，不需要在外部借助其他Listener
        /*//监听滚动，主要和加载相关
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollDetector());*/



        //mSwipeRefreshLayout.setOnRefreshListener(this);
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
            //onRefresh();
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
        /*mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });*/
    }

    /**
     * 隐藏progressbar
     */
    @Override
    public void hideProgress() {
        /*mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });*/
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
            //mListAdapter.notifyDataSetChanged();
            //mListAdapter2.notifyDataSetChanged();
            //会调用WrapAdapter的notifyDataSetChanged()
            mListAdapter2.notifyDataSetChanged();
            page++;
            mRecyclerView.onStopRefresh();
            mRecyclerView.onStopLoad();
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
        //mListAdapter.notifyItemRangeInserted(start, bookInfoResponses.size());
        //mListAdapter2.notifyDataSetChanged();
        mListAdapter2.notifyDataSetChanged();
        page++;
        mRecyclerView.onStopRefresh();
        mRecyclerView.onStopLoad();
    }

    /**
     * 下拉刷新时，从头请求数据
     */
    /*@Override
    public void onRefresh() {
        bookListPresenter.loadBooks(null, tag, 0, count, fields);
    }*/



    /*public void onLoadMore(){
        if(!mSwipeRefreshLayout.isRefreshing()){
            bookListPresenter.loadBooks(null, tag, page * count, count, fields);
        }
    }*/

    @Override
    public void onRefresh() {
        bookListPresenter.loadBooks(null, tag, 0, count, fields);
    }

    @Override
    public void onLoad() {
        bookListPresenter.loadBooks(null, tag, page * count, count, fields);
    }

    /**
     * 用于监听RecyclerView的滑动
     *
     */
    /*class RecyclerViewScrollDetector extends RecyclerView.OnScrollListener{
        private int lastVisibleItem;
        private int mScrollThreshold = DensityUtils.dp2px(getActivity(), 1);

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //如果没在滑动 而且 在最底部
            if(newState == RecyclerView.SCROLL_STATE_IDLE
                    && *//*lastVisibleItem + 1 == mListAdapter.getItemCount()*//*
                    lastVisibleItem + 1 == mAdapter.getItemCount()){
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
            //lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            lastVisibleItem = mLayoutManager2.findLastVisibleItemPosition();
        }
    }*/
}
