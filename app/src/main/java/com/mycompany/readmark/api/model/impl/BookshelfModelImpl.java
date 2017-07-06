package com.mycompany.readmark.api.model.impl;

import android.content.ContentValues;
import android.database.Cursor;

import com.mycompany.readmark.api.ApiCompleteListener;
import com.mycompany.readmark.api.commen.DatabaseHelper;
import com.mycompany.readmark.api.model.IBookshelfModel;
import com.mycompany.readmark.bean.http.BaseResponse;
import com.mycompany.readmark.bean.table.Bookshelf;
import com.mycompany.readmark.utils.commen.UIUtils;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Lenovo.
 */

public class BookshelfModelImpl implements IBookshelfModel{
    private SqlBrite sqlBrite = SqlBrite.create();
    private BriteDatabase db = sqlBrite
            .wrapDatabaseHelper(DatabaseHelper.getInstance(UIUtils.getContext()), Schedulers.io());
    private Subscription subscribe;


    /**
     * 取出表中所有条目
     * @param listener 回调
     */
    @Override
    public void loadBookshelf(final ApiCompleteListener listener) {
        if(db != null){
            final Observable<SqlBrite.Query> bookshelf = db
                    .createQuery("bookshelf", "SELECT * FROM bookshelf order by id DESC");
            subscribe = bookshelf.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SqlBrite.Query>() {
                        @Override
                        public void call(SqlBrite.Query query) {
                            Cursor cursor = query.run();
                            if(cursor == null || cursor.getCount() < 0){
                                return;
                            }
                            List<Bookshelf> bookshelfs = new ArrayList<Bookshelf>();
                            while(cursor.moveToNext()){
                                Bookshelf bookshelfBean = new Bookshelf();
                                bookshelfBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                                bookshelfBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                                bookshelfBean.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                                bookshelfBean.setCreateTime(cursor.getString(cursor.getColumnIndex("create_at")));
                                bookshelfBean.setColor(cursor.getInt(cursor.getColumnIndex("color")));
                                bookshelfBean.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
                                bookshelfBean.setProgress(cursor.getFloat(cursor.getColumnIndex("progress")));
                                bookshelfBean.setWaveratio(cursor.getFloat(cursor.getColumnIndex("waveratio")));
                                bookshelfBean.setAmpratio(cursor.getFloat(cursor.getColumnIndex("ampratio")));
                                bookshelfBean.setTotalpage(cursor.getInt(cursor.getColumnIndex("totalpage")));
                                bookshelfBean.setCurrentpage(cursor.getInt(cursor.getColumnIndex("currentpage")));
                                bookshelfBean.setRed(cursor.getInt(cursor.getColumnIndex("red")));
                                bookshelfBean.setGreen(cursor.getInt(cursor.getColumnIndex("green")));
                                bookshelfBean.setBlue(cursor.getInt(cursor.getColumnIndex("blue")));

                                bookshelfs.add(bookshelfBean);
                            }
                            listener.onCompleted(bookshelfs);
                        }
                    });


        }else{
            listener.onFailed(new BaseResponse(500, "db error : not init"));
        }
    }

    /**
     * 添加一个条目
     * @param bookshelf
     * @param listener 回调
     */
    @Override
    public void addBookshelf(Bookshelf bookshelf, ApiCompleteListener listener) {
        if (db != null) {
            //Log.e("执行到了", "插入数据");
            ContentValues values = new ContentValues();
            values.put("title", bookshelf.getTitle());
            values.put("remark", bookshelf.getRemark());
            values.put("create_at", bookshelf.getCreateTime());
            values.put("color", bookshelf.getColor());
            values.put("finished", bookshelf.getFinished());
            values.put("progress", bookshelf.getProgress());
            values.put("waveratio", bookshelf.getWaveratio());
            values.put("ampratio", bookshelf.getAmpratio());
            values.put("totalpage", bookshelf.getTotalpage());
            values.put("currentpage", bookshelf.getCurrentpage());
            values.put("red", bookshelf.getRed());
            values.put("green", bookshelf.getGreen());
            values.put("blue", bookshelf.getBlue());

            db.insert("bookshelf", values);

            listener.onCompleted("添加成功");
        } else {
            listener.onFailed(new BaseResponse(500, "db error : add"));
        }
    }

    /**
     * 更新一个条目
     * @param bookshelf bookshelf
     * @param listener  回调
     */
    @Override
    public void updateBookshelf(Bookshelf bookshelf, ApiCompleteListener listener) {
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("title", bookshelf.getTitle());
            values.put("remark", bookshelf.getRemark());
            values.put("create_at", bookshelf.getCreateTime());
            values.put("color", bookshelf.getColor());
            values.put("finished", bookshelf.getFinished());
            values.put("progress", bookshelf.getProgress());
            values.put("waveratio", bookshelf.getWaveratio());
            values.put("ampratio", bookshelf.getAmpratio());
            values.put("totalpage", bookshelf.getTotalpage());
            values.put("currentpage", bookshelf.getCurrentpage());
            values.put("red", bookshelf.getRed());
            values.put("green", bookshelf.getGreen());
            values.put("blue", bookshelf.getBlue());
            db.update("bookshelf", values, "id=?", bookshelf.getId() + "");
            //listener.onCompleted("更新成功");
        } else {
            listener.onFailed(new BaseResponse(500, "db error : update"));
        }
    }

    /**
     * 排序
     * @param id       id
     * @param front    前一个bookshelf order
     * @param behind   后一个bookshelf order
     * @param listener 回调
     */
    @Override
    public void orderBookshelf(int id, long front, long behind, ApiCompleteListener listener) {
        if (db != null) {
            long mOrder = front + (behind - front) / 2;
            ContentValues values = new ContentValues();
            values.put("orders", mOrder);
            db.update("bookshelf", values, "id=?", id + "");
        } else {
            listener.onFailed(new BaseResponse(500, "db error : update"));
        }
    }

    /**
     * 删除一个条目
     * @param id       id
     * @param listener 回调
     */
    @Override
    public void deleteBookshelf(String id, ApiCompleteListener listener) {
        if (db != null) {
            db.delete("bookshelf", "id=?", id);
        } else {
            listener.onFailed(new BaseResponse(500, "db error : delete"));
        }
    }

    @Override
    public void unSubscribe() {
        if (subscribe != null) {
            subscribe.unsubscribe();
            db.close();
        }
    }
}
