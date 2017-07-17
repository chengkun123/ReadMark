package com.mycompany.readmark.themechangeframeV2.skin;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;


import com.mycompany.readmark.themechangeframeV2.skin.attr.SkinAttr;
import com.mycompany.readmark.themechangeframeV2.skin.attr.SkinView;
import com.mycompany.readmark.themechangeframeV2.skin.callback.ISkinChangeListener;
import com.mycompany.readmark.themechangeframeV2.skin.support.SkinAppCompatViewInflater;
import com.mycompany.readmark.themechangeframeV2.skin.support.SkinAttrSupport;
import com.mycompany.readmark.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo.
 */

public abstract class BaseSkinActivity extends BaseActivity implements LayoutInflaterFactory, ISkinChangeListener {

    private static final String TAG = "BaseSkinActivity";
    private SkinAppCompatViewInflater mAppCompatViewInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //在setContentView之前设置好工厂
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LayoutInflaterCompat.setFactory(layoutInflater, this);
        SkinManager.getInstance().init(this);
        /*if (layoutInflater.getFactory() == null) {
            LayoutInflaterCompat.setFactory(layoutInflater, this);
        }*/
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        //1.创建View
        View view = createView(parent, name, context, attrs);

        Log.e(TAG, view + "");

        //2.解析属性 background textColor... 自定义
        if(view != null){
            //先取出所有可能有用的属性
            List<SkinAttr> skinAttrs = SkinAttrSupport.getAttrs(context, attrs);
            //生成SkinView
            SkinView skinView = new SkinView(view, skinAttrs);
            //3.统一交给SkinManager设置
            manageSkinView(skinView);

            //判断是否需要换肤
            SkinManager.getInstance().checkChangeSkin(skinView);
        }
        return view;
    }

    /*
    * 管理SkinView
    *
    * */
    private void manageSkinView(SkinView skinView) {
        //添加到Manager中
        List<SkinView> skinViews = SkinManager.getInstance().getSkinViews(this);
        if(skinViews == null){
            skinViews = new ArrayList<>();
            SkinManager.getInstance().register(this, skinViews);
        }
        skinViews.add(skinView);
    }


    public View createView(View parent, final String name, @NonNull Context context,
                           @NonNull AttributeSet attrs) {
        final boolean isPre21 = Build.VERSION.SDK_INT < 21;

        if (mAppCompatViewInflater == null) {
            mAppCompatViewInflater = new SkinAppCompatViewInflater();
        }

        // We only want the View to inherit its context if we're running pre-v21
        final boolean inheritContext = isPre21 && shouldInheritContext((ViewParent) parent);

        return mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true, /* Read read app:theme as a fallback at all times for legacy reasons */
                VectorEnabledTintResources.shouldBeUsed() /* Only tint wrap the context if enabled */
        );
    }


    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            // The initial parent is null so just return false
            return false;
        }
        final View windowDecor = getWindow().getDecorView();
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true;
            } else if (parent == windowDecor || !(parent instanceof View)
                    || ViewCompat.isAttachedToWindow((View) parent)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false;
            }
            parent = parent.getParent();
        }
    }

    @Override
    public void onSkinChange(SkinResource skinResource) {

    }


    @Override
    protected void onDestroy() {

        SkinManager.getInstance().unregister(this);
        super.onDestroy();
    }
}
