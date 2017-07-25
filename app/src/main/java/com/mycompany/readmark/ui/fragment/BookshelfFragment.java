package com.mycompany.readmark.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;


import com.mycompany.readmark.R;
import com.mycompany.readmark.api.presenter.impl.BookshelfPresenterImpl;
import com.mycompany.readmark.api.view.IBookListView;
import com.mycompany.readmark.bean.table.Bookshelf;
import com.mycompany.readmark.ui.activity.MainActivity;
import com.mycompany.readmark.ui.adapter.BookshelfAdapter;
import com.mycompany.readmark.ui.widget.recyclerviewdecoration.StaggeredGridDecoration;
import com.mycompany.readmark.utils.commen.DensityUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lenovo.
 */

public class BookshelfFragment extends BaseFragment implements IBookListView
        , SwipeRefreshLayout.OnRefreshListener
        , BookshelfAdapter.OnAdjustmentConfirmListener
        , BookshelfAdapter.OnDeleteConfirmListener{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    /*@BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;*/

    private GridLayoutManager mLayoutManager;
    private BookshelfAdapter mBookshelfAdapter;
    private BookshelfPresenterImpl mBookshelfPresenter;
    private int spanCount = 1;
    private List<Bookshelf> mBookshelfs;

    public static BookshelfFragment newInstance() {

        Bundle args = new Bundle();

        BookshelfFragment fragment = new BookshelfFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_book_shelf, container, false);
    }

    @Override
    protected void initEvents() {

        mToolbar.setTitle("Bookshelf");
        spanCount = getResources().getInteger(R.integer.gallery_span_count);
        mBookshelfPresenter = new BookshelfPresenterImpl(this);


        /*mSwipeRefreshLayout.setColorSchemeResources(R.color.recycler_color1, R.color.recycler_color2,
                R.color.recycler_color3, R.color.recycler_color4);*/

        mLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mBookshelfAdapter.getItemColumnSpan(position);
            }
        });
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBookshelfs = new ArrayList<>();
        mBookshelfAdapter = new BookshelfAdapter(mBookshelfs, getActivity(), spanCount);
        mBookshelfAdapter.setOnAdjustmentConfirmListener(this);
        mBookshelfAdapter.setOnDeleteConfirmListener(this);
        mBookshelfAdapter.setOnBookshelfClickListener((BookshelfAdapter.OnBookshelfClickListener) getActivity());
        mRecyclerView.setAdapter(mBookshelfAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final int space = DensityUtils.dp2px(getActivity(), 4);
        mRecyclerView.addItemDecoration(new StaggeredGridDecoration(space, space, space, space, spanCount));
        onRefresh();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).setToolbar(mToolbar);
        //((MainActivity)getActivity()).setThemeSetter(mRootView);
    }

    @Override
    public void onDestroyView() {
        if(mRootView != null){
            //((MainActivity)getActivity()).clearThemeSetter(mRootView);
        }
        super.onDestroyView();
    }

    @Override
    protected void initData(boolean isSavedNull) {

    }

    @Override
    public void showMessage(String msg) {
        if(msg != null){
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showProgress() {
        /*mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });*/
    }

    @Override
    public void hideProgress() {
        /*mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });*/
    }

    @Override
    public void refreshData(Object result) {
        if(result instanceof List){
            mBookshelfs.clear();
            mBookshelfs.addAll((Collection<? extends Bookshelf>) result);
            mBookshelfAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addData(Object result) {

    }

    @Override
    public void onRefresh() {
        mBookshelfPresenter.loadBookshelf();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sort){
            if(mBookshelfAdapter.isSorting()){
                mBookshelfAdapter.setSorting(false);
                mBookshelfAdapter.notifyDataSetChanged();
            }else{
                mBookshelfAdapter.setSorting(true);
                mBookshelfAdapter.notifyDataSetChanged();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*class BookshelfTouchHelperCallback extends ItemTouchHelper.SimpleCallback{
        public BookshelfTouchHelperCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }*/

    @Override
    public void onAdjustmentConfirm(Bookshelf bookshelf, boolean succeed) {
        mBookshelfPresenter.updateBookshelf(bookshelf);
        if(succeed){
            showMessage("保存成功");
        }else{
            showMessage("已读页数格式错误");
        }

    }

    @Override
    public void onDeleteConfirm(Bookshelf bookshelf) {
        mBookshelfPresenter.deleteBookshelf(bookshelf.getId()+"");
    }
}
