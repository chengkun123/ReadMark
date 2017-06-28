package com.mycompany.readmark.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mycompany.readmark.R;
import com.mycompany.readmark.ui.fragment.BaseFragment;
import com.mycompany.readmark.ui.fragment.BookshelfFragment;
import com.mycompany.readmark.ui.fragment.HomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo.
 */

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
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

    private long lastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initNavView();

    }

    private void initNavView(){
        MenuItem item = mNavigationView.getMenu().findItem(R.id.nav_theme);
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        mThemeSwitch = (SwitchCompat) MenuItemCompat
                .getActionView(item)
                .findViewById(R.id.theme_switch);
        mThemeSwitch.setChecked(true);


    }


    public void setToolbar(Toolbar toolbar){
        if(toolbar != null){
            Log.e("调用了","....");

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
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawers();
        }
        if(!(mCurrentFragment instanceof HomeFragment)){
            switchFragment(mCurrentFragment, HomeFragment.newInstance());
            mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
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
}