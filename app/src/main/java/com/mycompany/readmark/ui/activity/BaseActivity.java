package com.mycompany.readmark.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.mycompany.readmark.BaseApplication;
import com.mycompany.readmark.R;
import com.mycompany.readmark.commen.Constant;
import com.mycompany.readmark.themechangeframe.DayNight;
import com.mycompany.readmark.themechangeframe.MyThemeChanger;
import com.mycompany.readmark.themechangeframe.ThemeChangeHelper;
import com.mycompany.readmark.utils.commen.SPUtils;
import com.mycompany.readmark.utils.commen.UIUtils;
import com.mycompany.readmark.utils.customtabs.SystemBarTintManager;

/**
 * Created by Lenovo.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    //这个变量其实是来保存当前显示的Activity，也就是最后创建的一个Activity
    public static BaseActivity activity;
    protected Toolbar mToolbar;
    protected static ThemeChangeHelper mThemeChangeHelper;
    //protected MyThemeChanger mMyThemeChanger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        ((BaseApplication) UIUtils.getContext()).addActivity(this);
        init();

    }

    /*private void initTheme() {
        if(mThemeChangeHelper.isDay()){
            setTheme(R.style.DayTheme);
        }else{
            setTheme(R.style.NightTheme);
        }
    }*/

    /*private void initThemeHelper() {
        //mMyThemeChanger = MyThemeChanger.getMyThemeChanger(activity);
        if(mThemeChangeHelper == null){
            mThemeChangeHelper = new ThemeChangeHelper(UIUtils.getContext());
        }
    }*/


    /**
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if(mToolbar != null){
            //设置Toolbar并使能回退
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activity = null;
    }

    /**
     * activity退出时将activity移出栈
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((BaseApplication) UIUtils.getContext()).removeActivity(this);
    }

    /**
     * 菜单按钮初始化
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(getMenuID(), menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init(){
        initData();
        initEvents();
        if(isInitSystemBar()){
            initSystemBar(this);
        }
    }

    /**
     * 绑定数据
     */
    protected void initData(){

    }

    /**
     * 初始化事件，必须有子类实现
     */
    protected abstract void initEvents();

    /**
     * 是否初始化状态栏
     *
     * @return
     */
    protected boolean isInitSystemBar() {
        return true;
    }

    /**
     *
     * @param activity
     */
    private void initSystemBar(Activity activity){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                setTranslucentStatus(activity, true);
            }
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(getStatusColor());
    }

    /**
     * 状态栏的颜色
     * 子类可以通过复写这个方法来修改状态栏颜色
     *
     * @return ID
     */
    protected int getStatusColor() {
        return R.color.colorPrimaryDark;
        /*if (SPUtils.getPrefBoolean(Constant.THEME_MODEL, false)) {
            return R.color.colorPrimaryDarkNight;
        } else {
            return R.color.colorPrimaryDark;
        }*/
    }

    @TargetApi(19)
    protected void setTranslucentStatus(Activity activity, boolean on){
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        final int flag = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if(on){
            params.flags |= flag;
        }   else{
            params.flags &= ~flag;
        }

        window.setAttributes(params);
    }


    public Toolbar getToolbar(){
        return mToolbar;
    }

    /***
     * 默认toolbar不带menu，复写该方法指定menu
     *
     * @return
     */
    protected int getMenuID() {
        return R.menu.menu_empty;
    }

    /**
     * 是否显示菜单  默认显示
     *
     * @return
     */
    protected boolean showMenu() {
        return true;
    }
}

