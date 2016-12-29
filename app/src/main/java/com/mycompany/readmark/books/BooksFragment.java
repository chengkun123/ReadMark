package com.mycompany.readmark.books;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.mycompany.readmark.R;
import com.mycompany.readmark.common.DatabaseTableSingleton;
import com.mycompany.readmark.common.RetrofitSingleton;
import com.mycompany.readmark.search.SearchActivity;
import com.mycompany.readmark.search.SearchedInfoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lenovo on 2016/11/18.
 */
public class BooksFragment extends Fragment {

    private static int REQUEST_KEYWORD = 1;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private BooksAdapter mBooksAdapter;
    private FloatingActionButton mFabButton;
    private ProgressBar mProgressBar;

    private Observer<BookListBean> mObserver;
    private List<BooksBean> mDatas;

    private DatabaseTableSingleton mDatabaseTableSingleton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_books, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        initRecyclerView(view);
        initFab(view);
        initObserver();
        return view;
    }


    private void initRecyclerView(View view){
        mRecyclerView = (RecyclerView)view.findViewById(R.id.books_recyclerView);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mDatas = new ArrayList<BooksBean>();
        mBooksAdapter = new BooksAdapter(getActivity(), mDatas);
        mRecyclerView.setAdapter(mBooksAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void initFab(View view){
        mFabButton = (FloatingActionButton) view.findViewById(R.id.fab_normal);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(intent, REQUEST_KEYWORD);
            }
        });
    }

    private void initObserver(){
        //初始化观察者
        mObserver = new Observer<BookListBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BookListBean bookListBean) {
                Log.e("?????", "?????");
                //Toast.makeText(getContext(), "asdasd", Toast.LENGTH_SHORT).show();
                mDatas.clear();
                mDatas = bookListBean.getBooks();
                Log.e("?????", mDatas.get(0).getAuthor().get(0));
                mBooksAdapter.upDateBooks(mDatas);
            }
        };
    }

    private void doSearch(String keywords){
        Map<String, String> map = new HashMap<>();
        map.put("q", keywords);
        map.put("start", "0");
        map.put("count", "20");
        RetrofitSingleton.getRetrofit().getService()
                .getBookList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
    }

    private void saveSearchInfo(String keyword){
        SearchedInfoBean searchedInfoBean = new SearchedInfoBean();
        searchedInfoBean.setKeyWord(keyword);
        searchedInfoBean.setAuthor("");
        DatabaseTableSingleton.getDatabaseTable(getActivity())
                .saveSearchedInfo(searchedInfoBean);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2){
            if(requestCode == REQUEST_KEYWORD){
                saveSearchInfo(data.getStringExtra("search_keyword"));
                doSearch(data.getStringExtra("search_keyword"));
            }
        }
    }
}
