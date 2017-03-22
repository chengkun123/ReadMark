package com.mycompany.readmark.books;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mycompany.readmark.books.BooksAdapter;
import com.mycompany.readmark.books.BooksBean;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2017/1/3.
 */

/*
* 利用onInterceptTouchEvent回调调用我们自己的监听者的回调函数
* */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private BooksAdapter mBooksAdapter;
    private OnItemClickListener mListener;

    GestureDetector mGestureDetector;

    //这个OnTouchListener设置了一个接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position, BooksBean booksBean);
    }
    public RecyclerItemClickListener(Context context, OnItemClickListener listener, BooksAdapter booksAdapter) {
        mBooksAdapter = booksAdapter;
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        //找到点击的Item
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        //如果不为空，而且监听者不为空，而且mGestureDetector消费了该事件
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            //调用监听者的回调函数
            mListener.onItemClick(childView
                    , view.getChildAdapterPosition(childView)
                    , mBooksAdapter.getBook(view.getChildAdapterPosition(childView)));
        }

        //否则不拦截该手势
        return false;
    }
    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
    }
}
