package com.mycompany.readmark.ui.activity;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.ListView;

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

        NavigationView navigationView = (NavigationView) ((Activity)(mNavigationView.getContext())).getWindow().getDecorView().findViewById(R.id.nav_view);
        ViewGroup.LayoutParams params  = navigationView.getLayoutParams();

        if(savedInstanceState == null){
            mCurrentFragment = HomeFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .replace(R.id.fl_content, mCurrentFragment)
                    .commit();
        }
        mSkinManager = SkinManager.getInstance();

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_content);
        initNavView();
    }


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

        /*int menuId = R.menu.menu_empty;
        if(mCurrentFragment instanceof HomeFragment){
            //不知道加什么功能
        }else if(mCurrentFragment instanceof BookshelfFragment){
            menuId = R.menu.menu_bookshelf;
        }
        getMenuInflater().inflate(menuId, menu);
        mCurrentFragment.onCreateOptionsMenu(menu, getMenuInflater());*/
        return super.onCreateOptionsMenu(menu);
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