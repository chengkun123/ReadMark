package com.mycompany.readmark.ui;



import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mycompany.readmark.R;
import com.mycompany.readmark.books.BooksBean;
import com.mycompany.readmark.books.BooksFragment;
import com.mycompany.readmark.detail.BookDetailFragment;
import com.mycompany.readmark.search.SearchActivity;
import com.mycompany.readmark.widget.RecyclerItemClickListener;


public class MainActivity extends AppCompatActivity
        implements RecyclerItemClickListener.OnItemClickListener
        , BookDetailFragment.OnBackArrowPressedListener
        , BooksFragment.OnFabClickListener{
    //private Observer<BookListBean> mObserver;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    private BooksFragment mBooksFragment;
    private BookDetailFragment mBookDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        //使左上的图标发生变化
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        //还未初始化Drawer的点击事件

        //初始化Fragment
        mBooksFragment = new BooksFragment();

        if(savedInstanceState == null){
            //进入BooksFrangment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_content, mBooksFragment)
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //进入DetailFragment
    public void onItemClick(View view, int pos, BooksBean book){
        FragmentManager fm = getSupportFragmentManager();

        //Toast.makeText(this, "点击执行了", Toast.LENGTH_SHORT).show();
        mBookDetailFragment = BookDetailFragment.newInstance(book);
        fm.beginTransaction()
                .hide(mBooksFragment)
                .add(R.id.frame_content, mBookDetailFragment)
                .addToBackStack("main")
                .commit();
        Log.e("点击的Book的名称是", book.getTitle());
    }

    public void onBackArrowPressed(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .remove(mBookDetailFragment)
                .show(mBooksFragment)
                .commit();
    }

    public void onFabClick(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2){
            if(requestCode == 1){
                mBooksFragment.onActivityResult(1, 2, data);
            }
        }
    }

}
