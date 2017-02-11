package com.mycompany.readmark.search;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.mycompany.readmark.R;
import com.mycompany.readmark.common.DatabaseTableSingleton;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;


/**
 * Created by Lenovo on 2016/11/20.
 */
public class SearchActivity extends AppCompatActivity{

    private static String SEARCH_KEYWORD = "search_keyword";
    private static int RESULT_CODE = 2;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    //private RecyclerView mRecyclerView;
    //private LinearLayoutManager mLinearLayoutManager;
    //private SearchAdapter mSearchAdapter;
    private FlowLayout mFlowLayout;
    private List<String> mDatas = new ArrayList<>();
    private TagAdapter<String> mStringTagAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        mFlowLayout = (FlowLayout) findViewById(R.id.flow_layout);
        //mFlowLayout.setOnTagClickListener(this);
        //mFlowLayout.setOnTagLongClickListener(this);
        for(int i=0; i<10; i++){
            mDatas.add(i+"");
        }

        mStringTagAdapter = new TagAdapter<>(this, mDatas);
        mFlowLayout.setAdapter(mStringTagAdapter);
        //notify中才会进行View的重置
        mStringTagAdapter.notifyDataSetChanged();



        //initSearchedInfo(this);

        //mRecyclerView = (RecyclerView)findViewById(R.id.search_recyclerview);

        //mLinearLayoutManager = new LinearLayoutManager(this);
        //mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //mSearchAdapter = new SearchAdapter(this);
        //mRecyclerView.setAdapter(mSearchAdapter);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    public void onResume(){
        super.onResume();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);



        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //展开searchview
        mSearchView.setIconified(false);
        //显示默认框内标签
        mSearchView.setIconifiedByDefault(true);

        mToolbar.setTitle("");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent();
                intent.putExtra(SEARCH_KEYWORD, query);
                setResult(RESULT_CODE, intent);
                //SearchedInfoBean searchedInfoBean = new SearchedInfoBean();
                //searchedInfoBean.setKeyWord(query);
                //saveInfo(searchedInfoBean);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        finish();
    }


    private void initSearchedInfo(final Context context){
        //异步从数据库获取历史查询信息
        Observable.create(new Observable.OnSubscribe<List<SearchedInfoBean>>() {
            @Override
            public void call(Subscriber<? super List<SearchedInfoBean>> subscriber) {
                List<SearchedInfoBean> userList = null;
                userList = DatabaseTableSingleton.getDatabaseTable(context)
                        .loadSearchedInfo();
                subscriber.onNext(userList);
            }
        }).subscribe(new Action1<List<SearchedInfoBean>>() {
            @Override
            public void call(List<SearchedInfoBean> users) {
                Log.e("List的大小是", String.valueOf(users.size()));
                //mSearchAdapter.clearDatas();
                //mSearchAdapter.update(users);
                initTags(users);
            }
        });

    }

    private void initTags(List<SearchedInfoBean> list){
        //只添加10个数据
        for (int i=0; i<10; i++){
            if(!list.isEmpty() && list.size()-1-i >= 0){
                mDatas.add(list.get(list.size()-1-i).getKeyWord());
            }else{
                break;
            }
        }


        /*LayoutInflater inflater = LayoutInflater.from(this);
        for (int i=0; i<10; i++){
            if(!list.isEmpty() && list.size()-1-i >= 0){
                TextView textView = (TextView) inflater.inflate(R.layout.textview_tag, mFlowLayout, false);
                String temp = list.get(list.size()-1-i).getKeyWord();
                textView.setText(temp);
                mFlowLayout.addView(textView);
            }else{
                break;
            }
        }*/
    }

    /*public void onTagClick(){
        if(mStringTagAdapter.isDeleteShowed()){
            mStringTagAdapter.setIsDeleteShowed(false);
            mStringTagAdapter.notifyDataSetChanged();
        }else{
            mStringTagAdapter.setIsDeleteShowed(true);
            mStringTagAdapter.notifyDataSetChanged();
        }
    }*/

    /*public void onTagLongClick(){
        if(mStringTagAdapter.isDeleteShowed()){
            mStringTagAdapter.setIsDeleteShowed(false);
            mStringTagAdapter.notifyDataSetChanged();
        }else{
            mStringTagAdapter.setIsDeleteShowed(true);
            mStringTagAdapter.notifyDataSetChanged();
        }
    }*/
}
