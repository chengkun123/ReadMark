package com.mycompany.readmark.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.mycompany.readmark.themechangeframeV2.skin.SkinManager;
import com.mycompany.readmark.themechangeframeV2.skin.callback.ISkinChangeListener;
import com.mycompany.readmark.ui.activity.BookDetailActivity;
import com.mycompany.readmark.ui.adapter.BookListAdapter;
import com.mycompany.readmark.ui.adapter.BookListAdapter2;


import com.mycompany.readmark.ui.adapter.commen.ItemClickListener;
import com.mycompany.readmark.ui.adapter.commen.LoadRefreshRecyclerView;
import com.mycompany.readmark.ui.adapter.commen.MTRefreshCreator;
import com.mycompany.readmark.ui.adapter.commen.MultiTypeSupport;
import com.mycompany.readmark.ui.adapter.commen.RefreshRecyclerView;
import com.mycompany.readmark.ui.adapter.commen.SimpleLoadRefreshCreator;

import com.mycompany.readmark.ui.adapter.commen.WrapRecyclerAdapter;
import com.mycompany.readmark.ui.widget.bannerview.BannerDataAdapter;

import com.mycompany.readmark.ui.widget.bannerview.BannerView;
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
            //当Fragment销毁的时候，SkinManager不应该持有Fragment的View了
            /*SkinManager
                    .getInstance()
                    .clearRegisteredDetachedView((ISkinChangeListener) getActivity());*/
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
        //int spanCount = getResources().getInteger(R.integer.home_span_count);

        bookListPresenter = new BookListPresenterImpl(this);
        bookInfoResponses = new ArrayList<>();
        mLayoutManager2 = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager2);

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

        mRecyclerView.setAdapter(mListAdapter2);

        MTRefreshCreator creator = new MTRefreshCreator(getActivity(), mRecyclerView);
        mRecyclerView.addRefreshViewCreator(creator);
        mRecyclerView.setOnRefreshListener(this);

        //普通头部
        BannerView banner = (BannerView) LayoutInflater.from(getActivity())
                .inflate(R.layout.just_a_banner_view, mRecyclerView, false);
        List<String> bannerDatas = new ArrayList<>();
        bannerDatas.add("https://img3.doubanio.com/lpic/s29497411.jpg");
        bannerDatas.add("https://img3.doubanio.com/lpic/s29449712.jpg");
        bannerDatas.add("https://img3.doubanio.com/lpic/s29462473.jpg");
        bannerDatas.add("https://img1.doubanio.com/lpic/s29474288.jpg");
        List<String> bannerDescs = new ArrayList<>();
        bannerDescs.add("这个世界不欠你");
        bannerDescs.add("这样写出好故事");
        bannerDescs.add("意外的旅客");
        bannerDescs.add("时间地图");

        BannerDataAdapter bannerDataAdapter = new BannerDataAdapter(bannerDatas, getActivity(), bannerDescs);
        banner.setAdapter(bannerDataAdapter);
        banner.startRoll();
        mRecyclerView.addHeaderView(banner);

        SimpleLoadRefreshCreator creator1 = new SimpleLoadRefreshCreator(getActivity(), mRecyclerView);
        mRecyclerView.addLoadViewCreator(creator1);
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

        bookListPresenter.loadBooks(null, tag, 0, count, fields);
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
        mRecyclerView.onStoppingRefresh();
        mRecyclerView.onStoppingLoad();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示progressbar
     */
    @Override
    public void showProgress() {

    }

    /**
     * 隐藏progressbar
     */
    @Override
    public void hideProgress() {

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

            mListAdapter2.notifyDataSetChanged();
            page++;
            mRecyclerView.onStoppingRefresh();
            mRecyclerView.onStoppingLoad();
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
        mListAdapter2.notifyDataSetChanged();
        page++;
        mRecyclerView.onStoppingRefresh();
        mRecyclerView.onStoppingLoad();
    }



    @Override
    public void onRefresh() {
        bookListPresenter.loadBooks(null, tag, 0, count, fields);
    }

    @Override
    public void onLoad() {
        bookListPresenter.loadBooks(null, tag, page * count, count, fields);
    }

}
