package com.mycompany.readmark.themechangeframeV2.skin.support;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;


import com.mycompany.readmark.themechangeframeV2.skin.attr.SkinAttr;
import com.mycompany.readmark.themechangeframeV2.skin.attr.SkinType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo.
 */

public class SkinAttrSupport {
    private static final String TAG = "SkinAttrSupport";

    /**
     * 把这个View的属性中需要的(SkinType, entryName)对取出来（作为SkinAttr类）
     * @param context
     * @param attrs
     * @return
     */
    public static List<SkinAttr> getAttrs(Context context, AttributeSet attrs) {
        //把background、src、textColor解析出来
        List<SkinAttr> result = new ArrayList<>();

        //开始解析所有属性
        int length = attrs.getAttributeCount();
        for (int i=0; i<length; i++){
            //获取属性名称
            String name = attrs.getAttributeName(i);
            //获取属性值
            String value = attrs.getAttributeValue(i);
            //Log.e(TAG, name + "  "+ value);

            //检查是否存在所需属性名称
            SkinType skinType = getTargetSkinType(name);

            //如果是所需要的属性名
            if(skinType != null){
                //获取属性值名称
                String resName = getResName(context, value);

                if(TextUtils.isEmpty(resName)){
                    continue;
                }

                SkinAttr attr = new SkinAttr(resName, skinType);
                result.add(attr);
            }

        }
        return result;
    }

    /**
     * 根据属性值获取entry（属性值->id->entry）
     * (属性值还可以获取 package:type/entry)
     * @param context
     * @param value
     * @return
     */
    private static String getResName(Context context, String value) {
        if(value.startsWith("@")){
            //先把@去掉，然后获取id
            value = value.substring(1);
            //所以说id就是去掉@的字符串生成的
            int resId = Integer.parseInt(value);
            return context.getResources().getResourceEntryName(resId);//得到的是 entry（比如skin_night）
            //context.getResources().getResourceName(resId);//得到的是 package:type/entry
        }
        //不是@开头，直接返回
        return null;
    }

    /**
     * 通过name获取SkinType
     * @param name
     * @return
     */
    private static SkinType getTargetSkinType(String name) {
        //获取所有枚举的实例
        SkinType[] skinType = SkinType.values();
        for(SkinType type : skinType){
            if(type.getResName().equals(name)){
                return type;
            }
        }

        return null;
    }
}
