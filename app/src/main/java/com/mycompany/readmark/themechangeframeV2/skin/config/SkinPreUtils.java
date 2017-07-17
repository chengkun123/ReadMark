package com.mycompany.readmark.themechangeframeV2.skin.config;

import android.content.Context;
import android.util.Log;

/**
 * Created by Lenovo.
 */

public class SkinPreUtils {
    private static final String TAG = "SkinPreUtils";
    private static SkinPreUtils sSkinPreUtils;

    private Context mContext;

    private SkinPreUtils(Context context){
        mContext = context.getApplicationContext();
    }


    public static SkinPreUtils getInstance(Context context){
        if(sSkinPreUtils == null){
            synchronized (SkinPreUtils.class){
                if(sSkinPreUtils == null){
                    sSkinPreUtils = new SkinPreUtils(context);
                }
            }
        }
        return sSkinPreUtils;
    }

    /**
     * 保存皮肤路径
     * @param skinpath
     */
    public void saveSkinPath(String skinpath){
        Log.e(TAG,"保存了"+skinpath);
        mContext.getSharedPreferences(SkinConfig.SKIN_INFO_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(SkinConfig.SKIN_PATH_NAME, skinpath)
                .commit();
    }

    /**
     * 取出
     * @return
     */
    public String getSkinPath(){
        return mContext.getSharedPreferences(SkinConfig.SKIN_INFO_NAME, Context.MODE_PRIVATE)
                .getString(SkinConfig.SKIN_PATH_NAME, "");
    }

    /**
     * 清空皮肤路径
     */
    public void clearSkinPath() {
        saveSkinPath("");
    }
}
