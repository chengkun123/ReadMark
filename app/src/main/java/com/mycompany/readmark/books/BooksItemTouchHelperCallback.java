package com.mycompany.readmark.books;

import android.content.ClipData;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Lenovo on 2017/2/9.
 */
public class BooksItemTouchHelperCallback extends ItemTouchHelper.Callback{
    public static final float ALPHA_FULL = 1.0f;
    private OnItemMoveAndSwipeListener mOnItemMoveAndSwipeListener;
    private OnItemStateChangeListener mOnItemStateChangeListener;

    //由于涉及到数据的变动，定义一个接口，让Adapter去完成相应动作。
    public interface OnItemMoveAndSwipeListener{
        boolean onItemMove(int fromPos, int toPos);
        void onItemSwipe(int pos);
    }

    public interface OnItemStateChangeListener{
        void onItemSelected();
        void onItemDrop();
    }

    public BooksItemTouchHelperCallback(OnItemMoveAndSwipeListener onItemMoveAndSwipeListener){
        mOnItemMoveAndSwipeListener = onItemMoveAndSwipeListener;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //定义拖拽允许的方向和滑动允许的方向
        final int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(dragFlag, swipeFlag);
    }

    //在Item移动时调用
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        mOnItemMoveAndSwipeListener.onItemMove(viewHolder.getAdapterPosition()
                , target.getAdapterPosition());

        return true;
    }

    //在Item滑动时调用
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mOnItemMoveAndSwipeListener.onItemSwipe(viewHolder.getAdapterPosition());
    }

    //当长按选中item的时候（拖拽开始的时候）调用
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            if(viewHolder instanceof OnItemStateChangeListener){
                mOnItemStateChangeListener = (OnItemStateChangeListener)viewHolder;
                mOnItemStateChangeListener.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //当手指松开的时候（拖拽完成的时候）调用
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if(viewHolder instanceof OnItemStateChangeListener){
            mOnItemStateChangeListener = (OnItemStateChangeListener)viewHolder;
            mOnItemStateChangeListener.onItemDrop();
        }
    }
    /*这个方法可以判断当前是拖拽还是侧滑*/
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);

        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
