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
import android.widget.Button;

import com.mycompany.readmark.R;
import com.mycompany.readmark.books.BooksBean;
import com.mycompany.readmark.books.BooksFragment;
import com.mycompany.readmark.customview.FloatingActionButton.MultiFloatingActionButton;
import com.mycompany.readmark.customview.FloatingActionButton.TagFabLayout;
import com.mycompany.readmark.detail.BookDetailActivity;
import com.mycompany.readmark.detail.BookDetailFragment;
import com.mycompany.readmark.marker.MarkerActivity;
import com.mycompany.readmark.search.SearchActivity;
import com.mycompany.readmark.themechangeframe.DayNight;
import com.mycompany.readmark.themechangeframe.ThemeChangeHelper;
import com.mycompany.readmark.themechangeframe.ThemeChanger;
import com.mycompany.readmark.books.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity
        implements RecyclerItemClickListener.OnItemClickListener
        /*, BookDetailFragment.OnBackArrowPressedListener*/
        , BooksFragment.OnFabClickListener
        , NavigationView.OnNavigationItemSelectedListener
        , MultiFloatingActionButton.OnFabItemClickListener {
    //private Observer<BookListBean> mObserver;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private MultiFloatingActionButton mMultiFloatingActionButton;

    private BooksFragment mBooksFragment;
    private BookDetailFragment mBookDetailFragment;

    private ThemeChangeHelper mThemeChangeHelper;
    private ThemeChanger mThemeChanger;

    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initHelper();
        initTheme();
        setContentView(R.layout.activity_main);
        initViews();
        initFragment(savedInstanceState);
        initChanger();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getSupportFragmentManager().findFragmentById(R.id.frame_content) instanceof BooksFragment){
            mMultiFloatingActionButton.setVisibility(View.VISIBLE);
            //mToolbar.setVisibility(View.VISIBLE);
        }
        
    }

    private void initHelper(){
        mThemeChangeHelper = new ThemeChangeHelper(this);
    }

    private void initTheme(){
        if(mThemeChangeHelper.isDay()){
            setTheme(R.style.DayTheme);
        }else{
            setTheme(R.style.NightTheme);
        }
    }

    private void initViews(){
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //testButton = (Button) findViewById(R.id.test_button);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        //使左上的图标发生变化
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);


        mMultiFloatingActionButton = (MultiFloatingActionButton) findViewById(R.id.floating_button);
        mMultiFloatingActionButton.setOnFabItemClickListener(this);
    }
    private void initFragment(Bundle savedInstanceState){
        mBooksFragment = new BooksFragment();
        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_content, mBooksFragment)
                    .commit();
        }
    }

    private void initChanger(){
        mThemeChanger = new ThemeChanger.Builder(this)
                .addSchemedBgColorSetter(R.id.navigation_view, R.attr.myBackground)
                .addSchemedBgColorSetter(R.id.main_app_bar, R.attr.myBackground)
                .addSchemedBgColorSetter(R.id.frame_content, R.attr.myBackground)
                .create();
    }



    public void onBackArrowPressed(){
        mMultiFloatingActionButton.setVisibility(View.VISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .remove(mBookDetailFragment)
                .show(mBooksFragment)
                .commit();
    }

    public void onFabClick(){
        Log.e("点击了按钮", "");
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_item_book:
                if (mThemeChangeHelper.isDay()) {
                    Log.e("变成晚上", "..");
                    mThemeChangeHelper.setMode(DayNight.NIGHT);

                    mThemeChanger.setTheme(R.style.NightTheme);
                    //testButton.setBackgroundColor(Color.LTGRAY);


                } else {
                    Log.e("变成白天", "..");
                    mThemeChangeHelper.setMode(DayNight.DAY);

                    mThemeChanger.setTheme(R.style.DayTheme);
                    //testButton.setBackgroundColor(0);
                }
                break;
        }
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onFabItemClick(TagFabLayout view, int pos) {
        switch (pos){
            case 4:
                Intent search = new Intent(this, SearchActivity.class);
                startActivityForResult(search, 1);
                break;
            case 3:
                Intent marker = new Intent(this, MarkerActivity.class);
                startActivity(marker);
        }
    }


    //进入DetailActivity
    public void onItemClick(View view, int pos, BooksBean book){
        mMultiFloatingActionButton.setVisibility(View.INVISIBLE);
        mToolbar.setVisibility(View.INVISIBLE);

        Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);

        /*FragmentManager fm = getSupportFragmentManager();
        mBookDetailFragment = BookDetailFragment.newInstance(book);
        fm.beginTransaction()
                .hide(mBooksFragment)
                .add(R.id.frame_content, mBookDetailFragment)
                .addToBackStack("main")
                .commit();
        Log.e("点击的Book的名称是", book.getTitle());*/
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().findFragmentById(R.id.frame_content) instanceof BooksFragment){
            mMultiFloatingActionButton.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
        }
        Log.e("按了回退","按了");
    }
}
