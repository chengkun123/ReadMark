package com.mycompany.readmark.themechangeframeV2.skin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 用于从插件包获取Drawable和Color的类
 */

public class SkinResource {
    private Resources mSkinResources;
    private String mPackageName;

    public SkinResource(Context context, String path) {
        try {
            //获取包名

            mPackageName = context.getPackageManager()
                    .getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
                    .packageName;

            //默认Resource
            Resources superresources = context.getResources();
            //反射获取AssetManager
            AssetManager asset = AssetManager.class.newInstance();
            //最主要的是添加一个路径
            Method method = asset.getClass().getDeclaredMethod("addAssetPath", String.class);
            //不是私有方法
            //接收者+参数，
            method.invoke(asset, path);

            //storage/emulated/0/skin.skin

            //新建一个我们的
            mSkinResources = new Resources(asset
                    , superresources.getDisplayMetrics()
                    , superresources.getConfiguration());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Drawable getDrawableByName(String name){
        try{
            int resId = mSkinResources.getIdentifier(name, "drawable", mPackageName);
            Drawable drawable = mSkinResources.getDrawable(resId);
            return drawable;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取颜色
     * @param resName
     * @return
     */
    public ColorStateList getColorByName(String resName){
        try{
            int resId = mSkinResources.getIdentifier(resName, "color", mPackageName);
            ColorStateList color = mSkinResources.getColorStateList(resId);
            return color;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
