package com.mycompany.readmark.themechangeframeV2.skin.attr;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycompany.readmark.themechangeframeV2.skin.SkinManager;
import com.mycompany.readmark.themechangeframeV2.skin.SkinResource;


/**
 * 枚举类，有新的View的属性直接在这里增加
 */

public enum SkinType {
    TEXT_COLOR("textColor") {
        @Override
        public void skin(View view, String resName) {
            //获取颜色
            SkinResource resource = getSkinResource();
            ColorStateList color = resource.getColorByName(resName);
            if(color != null){
                TextView textView = (TextView) view;
                textView.setTextColor(color);
                return ;
            }
        }
    }, BACKGROUND("background") {
        @Override
        public void skin(View view, String resName) {
            //获取背景
            SkinResource resource = getSkinResource();

            //如果是个图片
            Drawable drawable = resource.getDrawableByName(resName);
            if(drawable != null){

                ImageView imageView = (ImageView) view;
                imageView.setBackgroundDrawable(drawable);
                return;
            }
            //如果是颜色
            ColorStateList color = resource.getColorByName(resName);
            if(color != null){
                view.setBackgroundColor(color.getDefaultColor());
                return;
            }


        }
    }, SRC("src") {
        @Override
        public void skin(View view, String resName) {
            SkinResource resource = getSkinResource();
            Drawable drawable = resource.getDrawableByName(resName);
            if(drawable != null){
                ImageView imageView = (ImageView) view;
                //注意src的设置
                imageView.setImageDrawable(drawable);
                return;
            }

        }
    }, CARDBACKGROUNDCOLOR("cardBackgroundColor") {
        @Override
        public void skin(View view, String resName) {
            Log.e("CardView", "成功切换");
            SkinResource resource = getSkinResource();
            //如果是颜色
            ColorStateList color = resource.getColorByName(resName);
            if(color != null){
                ((CardView)view).setCardBackgroundColor(color.getDefaultColor());
                return;
            }
        }
    }, TABTEXTCOLOR("tabTextColor") {
        @Override
        public void skin(View view, String resName) {
            Log.e("TabView", "成功切换");
            SkinResource resource = getSkinResource();
            //如果是颜色
            ColorStateList color = resource.getColorByName(resName);
            if(color != null){
                ((TabLayout)view).setTabTextColors(color);
                return;
            }
        }
    };



    private String mResName;

    SkinType(String resName){
        this.mResName = resName;
    }


    public abstract void skin(View view, String resName);

    public String getResName() {
        return mResName;
    }

    public SkinResource getSkinResource(){
        return SkinManager.getInstance().getSkinResource();
    }

}
