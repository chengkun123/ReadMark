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
import android.view.Menu;
import android.view.MenuItem;



import com.mycompany.readmark.R;
import com.mycompany.readmark.common.DatabaseTableSingleton;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;


/**
 * Created by Lenovo on 2016/11/20.
 */
public class SearchActivity extends AppCompatActivity {

    private static String SEARCH_KEYWORD = "search_keyword";
    private static int RESULT_CODE = 2;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SearchAdapter mSearchAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        mRecyclerView = (RecyclerView)findViewById(R.id.search_recyclerview);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSearchAdapter = new SearchAdapter(this);
        mRecyclerView.setAdapter(mSearchAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        showSearchedInfo(this);

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

    //异步从数据库获取
    private void showSearchedInfo(final Context context){
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
                mSearchAdapter.clearDatas();
                mSearchAdapter.update(users);
            }
        });
    }

}
