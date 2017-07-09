package com.mycompany.readmark.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.mycompany.readmark.R;
import com.mycompany.readmark.themechangeframe.ThemeChangeHelper;
import com.mycompany.readmark.ui.adapter.TagAdapter;
import com.mycompany.readmark.ui.widget.FlowLayout;
import com.mycompany.readmark.utils.commen.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo.
 */

public class SearchActivity extends AppCompatActivity implements FlowLayout.OnTagClickListener{

    private static String SEARCH_KEYWORD = "search_keyword";
    private static int RESULT_CODE = 2;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private FlowLayout mFlowLayout;
    private List<String> mDatas = new ArrayList<>();
    private TagAdapter<String> mStringTagAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ThemeChangeHelper helper = new ThemeChangeHelper(this);
        /*if (helper.isDay()){
            setTheme(R.style.DayTheme);
        }else{
            setTheme(R.style.NightTheme);
        }*/
        initTheme();
        setContentView(R.layout.activity_search);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        mFlowLayout = (FlowLayout) findViewById(R.id.flow_layout);
        mDatas.add("二手时间");
        mDatas.add("极简宇宙史");
        mDatas.add("无人生还");
        mDatas.add("灯塔");
        mDatas.add("活着为了讲述");
        mDatas.add("造房子");
        mDatas.add("北鸢");
        mDatas.add("斯通纳");
        mDatas.add("我可以咬一口吗");
        mDatas.add("我脑袋里的怪东西");

        mStringTagAdapter = new TagAdapter<>(this, mDatas);
        mFlowLayout.setAdapter(mStringTagAdapter);
        mFlowLayout.setOnTagClickListener(this);
        mStringTagAdapter.notifyDataSetChanged();
    }

    public void onResume(){
        super.onResume();

    }
    private void initTheme() {
        if(ThemeChangeHelper.getThemeChangeHelper(UIUtils.getContext()).isDay()){
            setTheme(R.style.DayTheme);
        }else{
            setTheme(R.style.NightTheme);
        }
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
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("q", query);
                UIUtils.startActivity(intent);
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
    public void onTagClick(String input) {
        //Log.e("回调了",input);
        mSearchView.setQuery(input, false);
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


}