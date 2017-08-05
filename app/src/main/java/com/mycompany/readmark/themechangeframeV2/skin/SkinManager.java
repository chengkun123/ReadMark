package com.mycompany.readmark.themechangeframeV2.skin;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;


import com.mycompany.readmark.themechangeframeV2.skin.attr.SkinView;
import com.mycompany.readmark.themechangeframeV2.skin.callback.ISkinChangeListener;
import com.mycompany.readmark.themechangeframeV2.skin.config.SkinConfig;
import com.mycompany.readmark.themechangeframeV2.skin.config.SkinPreUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lenovo.
 */

public class SkinManager {
    private static SkinManager sInstance;
    private Context mContext;
    //收集了Activity也同时收集了监听者
    private Map<ISkinChangeListener, List<SkinView>> mSkinViews = new HashMap<>();
    //指定的皮肤资源
    private SkinResource mSkinResource;


    static {
        sInstance = new SkinManager();
    }

    public static SkinManager getInstance() {
        return sInstance;
    }


    public void init(Context context){
        mContext = context.getApplicationContext();

        //做一系列措施防止皮肤被删
        String currentPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        String appPath = mContext.getPackageResourcePath();
        if(!currentPath.equals(appPath)){
            File file = new File(currentPath);
            if(!file.exists()){
                SkinPreUtils.getInstance(mContext).clearSkinPath();
                return;
            }
        }
        //做一下能不能获取包名的判断
        String packageName = context.getPackageManager()
                .getPackageArchiveInfo(currentPath, PackageManager.GET_ACTIVITIES)
                .packageName;
        if(TextUtils.isEmpty(packageName)){
            SkinPreUtils.getInstance(mContext).clearSkinPath();
            return;
        }
        //校验签名



        //初次进入mSkinResource并没有被初始化
        mSkinResource = new SkinResource(mContext, currentPath);

    }

    /**
     * 加载皮肤
     * @param path
     * @return
     */
    public int loadSkin(String path) {
        File file = new File(path);
        if(!file.exists()){
            return SkinConfig.SKIN_FILE_NOEXIST;
        }
        //path的包名
        String packageName = mContext.getPackageManager()
                .getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
                .packageName;
        //这个path下并不是一个apk，获取不到包名
        if(TextUtils.isEmpty(packageName)){
            SkinPreUtils.getInstance(mContext).clearSkinPath();
            return SkinConfig.SKIN_FILE_ERROR;
        }

        String currenPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        if(path.equals(currenPath)){
            return SkinConfig.SKIN_CHANGE_NOTHING;
        }

        //校验签名


        //初始化资源Resources
        mSkinResource = new SkinResource(mContext, path);

        changeSkin();

        //更换完之后保存皮肤的路径
        saveSkinStatus(path);

        return 0;
    }

    private void changeSkin() {
        Set<ISkinChangeListener> keys = mSkinViews.keySet();

        for(ISkinChangeListener key : keys){
            List<SkinView> skinViews = mSkinViews.get(key);
            for(SkinView skinView : skinViews){
                skinView.skin();
            }

            //在每一个Activity的View换肤的时候，通知Activity
            key.onSkinChange(mSkinResource);

        }
    }

    private void saveSkinStatus(String path) {
        SkinPreUtils.getInstance(mContext).saveSkinPath(path);
    }

    /**
     *
     * 恢复默认
     * @return
     */
    public int restoreDefault(){
        //判断当前有没有皮肤
        String currentPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        if(TextUtils.isEmpty(currentPath)){
            return SkinConfig.SKIN_CHANGE_NOTHING;
        }
        //恢复成当前运行的app的路径
        String skinPath = mContext.getPackageResourcePath();
        //重新创建Resource
        mSkinResource = new SkinResource(mContext, skinPath);

        changeSkin();
        SkinPreUtils.getInstance(mContext).clearSkinPath();

        return SkinConfig.SKIN_CHANGE_SUCCESS;
    }



    public List<SkinView> getSkinViews(Activity activity) {
        return mSkinViews.get(activity);
    }

    /**
     * @param activity
     * @param skinviews
     */
    public void register(ISkinChangeListener activity, List<SkinView> skinviews) {
        mSkinViews.put(activity, skinviews);
    }

    /**
     * Activity退出时，释放其持有的需要改变的View
     * @param activity
     */
    public void unregister(ISkinChangeListener activity){
        mSkinViews.remove(activity);
    }

    /**
     * 针对Fragment，此时其Activity没有销毁，但Fragment销毁，其View也需要移除
     * @param listener
     */
    public void clearRegisteredDetachedView(ISkinChangeListener listener){
        List<SkinView> targets = mSkinViews.get(listener);
        if(targets != null){
            for(SkinView skinView : targets){
                //如果这个View已经被remove
                if(skinView.getView().getParent() == null){
                    targets.remove(skinView);
                }
            }
        }
    }

    /**
     * 获取皮肤资源
     * @return
     */
    public SkinResource getSkinResource() {
        return mSkinResource;
    }

    public void checkChangeSkin(SkinView skinView) {
        String currentPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        if(!TextUtils.isEmpty(currentPath)){
            skinView.skin();
        }

    }
}
