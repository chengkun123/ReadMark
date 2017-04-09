package com.mycompany.readmark.themechangeframe;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lenovo on 2017/2/16.
 */
public class ListAndRecyclerSetter extends ViewSetter{

    protected Set<ViewSetter> mItemChildViewSetters = new HashSet<>();

    public ListAndRecyclerSetter(View targetView){
        this(targetView, 0);
    }

    public ListAndRecyclerSetter(View targetView, int attrId){
        super(targetView, attrId);
    }

    public ListAndRecyclerSetter addSchemedChildViewBgColor(int viewId, int attrId){
        mItemChildViewSetters.add(new ViewBgColorSetter(viewId, attrId));
        return this;
    }

    public ListAndRecyclerSetter addSchemedChildViewTextColor(int viewId, int attrId){
        mItemChildViewSetters.add(new ViewTextColorSetter(viewId, attrId));
        return this;
    }

    @Override
    public void setValue(Resources.Theme theme, int themeId) {
        //clearCachePool(mTargetView);
        changeChildrenAttrs((ViewGroup) mTargetView, theme, themeId);
        clearCachePool(mTargetView);
    }



    private void changeChildrenAttrs(ViewGroup targetViewGroup, Resources.Theme theme, int themeId){
        int count = targetViewGroup.getChildCount();
        //遍历到每一个item
        for(int i = 0; i<count; i++){
            View childView = targetViewGroup.getChildAt(i);
            for(ViewSetter setter : mItemChildViewSetters){
                setter.mTargetView = ((ViewGroup)childView).findViewById(setter.mTargetViewId);
                setter.setValue(theme, themeId);
            }
        }
    }

    private void clearCachePool(View rootView){
        if(rootView instanceof AbsListView){
            try{
                Field field = AbsListView.class
                        .getDeclaredField("mRecycler");
                field.setAccessible(true);
                Method method = Class
                        .forName("android.widget.AbslistView$RecycleBin")
                        .getDeclaredMethod("clear", new Class[0]);
                method.setAccessible(true);
                method.invoke(field.get(rootView), new Object[0]);
                Log.e("", "清空AbsListView的RecycleBin");
            }catch (NoSuchFieldException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }catch (NoSuchMethodException e){
                e.printStackTrace();
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }catch(InvocationTargetException e){
                e.printStackTrace();
            }
        }else if(rootView instanceof RecyclerView){
            try {
                Log.e("的确清理了缓存View", "ss");
                Field field = RecyclerView.class.getDeclaredField("mRecycler");
                field.setAccessible(true);

                //Recycler的clear
                Class<?> cl = Class.forName(RecyclerView.Recycler.class.getName());
                Method method = cl.getDeclaredMethod("clear", (Class<?>[])new Class[0]);
                method.setAccessible(true);
                method.invoke(field.get(rootView), new Object[0]);

                //RecycledViewPool clear
                RecyclerView.RecycledViewPool recycledViewPool = ((RecyclerView) rootView).getRecycledViewPool();
                recycledViewPool.clear();

            }catch (NoSuchFieldException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }catch (NoSuchMethodException e){
                e.printStackTrace();
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }catch (InvocationTargetException e){
                e.printStackTrace();
            }
        }
    }
}
