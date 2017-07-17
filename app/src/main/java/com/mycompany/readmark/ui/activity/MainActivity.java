package com.mycompany.readmark.ui.activity;

import android.app.Service;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mycompany.readmark.R;
import com.mycompany.readmark.bean.table.Bookshelf;
import com.mycompany.readmark.themechangeframe.DayNight;
import com.mycompany.readmark.themechangeframe.ListAndRecyclerSetter;
import com.mycompany.readmark.themechangeframe.MyThemeChanger;
import com.mycompany.readmark.themechangeframe.ThemeChangeHelper;
import com.mycompany.readmark.themechangeframe.ThemeChanger;
import com.mycompany.readmark.themechangeframe.ViewBgColorSetter;
import com.mycompany.readmark.themechangeframe.ViewTextColorSetter;
import com.mycompany.readmark.themechangeframeV2.skin.BaseSkinActivity;
import com.mycompany.readmark.themechangeframeV2.skin.SkinManager;
import com.mycompany.readmark.themechangeframeV2.skin.config.SkinConfig;
import com.mycompany.readmark.themechangeframeV2.skin.config.SkinPreUtils;
import com.mycompany.readmark.ui.adapter.BookshelfAdapter;
import com.mycompany.readmark.ui.fragment.BaseFragment;
import com.mycompany.readmark.ui.fragment.BookListFragment;
import com.mycompany.readmark.ui.fragment.BookshelfFragment;
import com.mycompany.readmark.ui.fragment.BookshelfProgressFragment;
import com.mycompany.readmark.ui.fragment.HomeFragment;
import com.mycompany.readmark.utils.commen.UIUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo.
 */

public class MainActivity extends BaseSkinActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , BookshelfAdapter.OnBookshelfClickListener {
    private static final int EXIT_APP_DELAY = 1000;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;



    private SwitchCompat mThemeSwitch;
    //private FloatingActionButton mFab;

    //记录当前正在显示的Fragment
    private BaseFragment mCurrentFragment;
    private FragmentManager mFragmentManager;

    //private MyThemeChanger mMyThemeChanger;
    //private ThemeChangeHelper mThemeChangeHelper;

    private SkinManager mSkinManager;
    private long lastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initTheme();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(mFragmentManager == null){
            mFragmentManager = getSupportFragmentManager();
        }

        if(savedInstanceState == null){
            mCurrentFragment = HomeFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .replace(R.id.fl_content, mCurrentFragment)
                    .commit();
        }
        mSkinManager = SkinManager.getInstance();
        /*if(mMyThemeChanger == null){
            mMyThemeChanger = MyThemeChanger.getMyThemeChanger(this);
        }*/
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_content);
        /*mMyThemeChanger.addViewSetter(new ViewBgColorSetter(frameLayout, R.attr.myBackground));
        mMyThemeChanger.addViewSetter(new ViewBgColorSetter(mNavigationView, R.attr.myBackground));*/
        initNavView();
    }

    /*private void initTheme() {
        mThemeChangeHelper= ThemeChangeHelper.getThemeChangeHelper(UIUtils.getContext());
        if(mThemeChangeHelper.isDay()){
            setTheme(R.style.DayTheme);
        }else{
            setTheme(R.style.NightTheme);
        }
    }*/

    private void initNavView(){
        MenuItem item = mNavigationView.getMenu().findItem(R.id.nav_theme);
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        mThemeSwitch = (SwitchCompat) MenuItemCompat
                .getActionView(item)
                .findViewById(R.id.theme_switch);
        //
        mThemeSwitch.setChecked(!SkinPreUtils.getInstance(this).getSkinPath().equals(""));


        mThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    /*
                    * 这里有待改进，是读取apk包的地方
                    * */
                    String skinPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + File.separator
                            + "skin_night.apk";
                    mSkinManager.loadSkin(skinPath);
                }else{
                    mSkinManager.restoreDefault();
                }

            }
        });
        /*mThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mThemeChangeHelper.setMode(isChecked ? DayNight.NIGHT : DayNight.DAY);
                mMyThemeChanger.setTheme(isChecked ? R.style.NightTheme : R.style.DayTheme);
            }
        });*/
    }


    public void setToolbar(Toolbar toolbar){
        if(toolbar != null){
            mToolbar = toolbar;
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                    , drawer
                    , toolbar
                    , R.string.navigation_drawer_open
                    , R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            mNavigationView.setNavigationItemSelectedListener(this);
        }
    }

    /*public void setThemeSetter(View rootView){
        //Log.e("setThemeSetter", "调用了");

        AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);
        if(appBarLayout != null){
            //Log.e("appbar", "不空");
            Log.e("Context是？", (appBarLayout.getContext() == MainActivity.this)+"");
            ViewBgColorSetter viewBgColorSetter = new ViewBgColorSetter(appBarLayout, R.attr.colorPrimary);
            mMyThemeChanger.addViewSetter(viewBgColorSetter);
        }
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if(toolbar != null){

            Log.e("Context是？", (toolbar.getContext() == MainActivity.this)+"");
            ViewBgColorSetter toolbarSetter = new ViewBgColorSetter(toolbar, R.attr.colorPrimary);
            mMyThemeChanger.addViewSetter(toolbarSetter);
        }
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        if(tabLayout != null){
            Log.e("Context是？", (tabLayout.getContext() == MainActivity.this)+"");
            ViewBgColorSetter tablayoutSetter = new ViewBgColorSetter(tabLayout, R.attr.colorPrimary);
            mMyThemeChanger.addViewSetter(tablayoutSetter);
            *//*ViewTextColorSetter tablayouttextSetter = new ViewTextColorSetter(tabLayout, R.attr.myTextColor);
            mMyThemeChanger.addViewSetter(tablayouttextSetter);*//*

        }

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        if(recyclerView != null){
            Log.e("Context是？", (recyclerView.getContext() == MainActivity.this)+"");
            //Log.e("recycle", "不空");
            ListAndRecyclerSetter listAndRecyclerSetter = new ListAndRecyclerSetter(recyclerView);
            listAndRecyclerSetter.addSchemedChildViewBgColor(R.id.book_list_cardview, R.attr.myBackground)
                    .addSchemedChildViewBgColor(R.id.book_shelf_cardview, R.attr.myBackground)
                    .addSchemedChildViewBgColor(R.id.book_list_item_layout, R.attr.myBackground)
                    .addSchemedChildViewBgColor(R.id.book_list_item_text_layout, R.attr.myBackground)
                    .addSchemedChildViewBgColor(R.id.book_list_desc_layout, R.attr.myBackground)
                    .addSchemedChildViewTextColor(R.id.tv_book_title, R.attr.myTextColor)
                    .addSchemedChildViewTextColor(R.id.tv_hots_num, R.attr.myTextColor)
                    .addSchemedChildViewTextColor(R.id.tv_book_info, R.attr.myTextColor)
                    .addSchemedChildViewTextColor(R.id.tv_book_description, R.attr.myTextColor);
            ViewBgColorSetter rviewBgColorSetter = new ViewBgColorSetter(recyclerView, R.attr.myBackground);
            mMyThemeChanger.addViewSetter(listAndRecyclerSetter);
            mMyThemeChanger.addViewSetter(rviewBgColorSetter);
        }
    }*/

    /*public void clearThemeSetter(View rootView){
        Log.e("clearThemeSetter", "调用了");
        mMyThemeChanger.deleteViewSetter(rootView);
    }*/


    /*public void setFab(FloatingActionButton fab) {
        mFab = fab;
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);

                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
            }
        });
    }*/

    @Override
    protected void initEvents() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_home){
            switchFragment(mCurrentFragment, HomeFragment.newInstance());
        }else if(id == R.id.nav_bookshelf){
            switchFragment(mCurrentFragment, BookshelfFragment.newInstance());
        }
        drawer.closeDrawers();
        return true;
    }

    private void switchFragment(BaseFragment from, BaseFragment to){
        if(from == to){
            return;
        }
        mCurrentFragment = to;
        mFragmentManager.beginTransaction()
                .replace(R.id.fl_content, to)
                .commit();
        //重绘menu
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        int menuId = R.menu.menu_empty;
        if(mCurrentFragment instanceof HomeFragment){
            //不知道加什么功能
        }else if(mCurrentFragment instanceof BookshelfFragment){
            menuId = R.menu.menu_bookshelf;
        }
        getMenuInflater().inflate(menuId, menu);
        mCurrentFragment.onCreateOptionsMenu(menu, getMenuInflater());
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        //分发点击事件给下层
        mCurrentFragment.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawers();
        }
        if(mCurrentFragment instanceof BookshelfFragment){
            switchFragment(mCurrentFragment, HomeFragment.newInstance());
            mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            return;
        }
        if(mCurrentFragment instanceof BookshelfProgressFragment){
            switchFragment(mCurrentFragment, BookshelfFragment.newInstance());
            mNavigationView.getMenu().findItem(R.id.nav_bookshelf).setChecked(true);
            return;
        }
        if((System.currentTimeMillis() - lastTime) > EXIT_APP_DELAY){
            lastTime = System.currentTimeMillis();
            Snackbar.make(drawer, getString(R.string.press_twice_exit), Snackbar.LENGTH_SHORT)
                    .setAction(R.string.exit_directly, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .show();
        }else{
            moveTaskToBack(true);
        }

    }

    /**
     *
     * 显示进度
     * @param bookshelf
     */
    @Override
    public void onBookshelfClick(Bookshelf bookshelf) {
        switchFragment(mCurrentFragment, BookshelfProgressFragment.newInstance(bookshelf));
    }
}