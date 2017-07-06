package com.mycompany.readmark.api.commen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "ReadMark.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper mDatabaseHelper = null;

    public static DatabaseHelper getInstance(Context context){
        if(mDatabaseHelper == null){
            //这里有可能切换
            synchronized (DatabaseHelper.class){
                if(mDatabaseHelper == null){
                    mDatabaseHelper = new DatabaseHelper(context);
                }
            }
        }
        return mDatabaseHelper;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists bookshelf(" +
                "id integer primary key," +
                "title varchar not null," + //标题
                "remark varchar," + //备注
                "create_at varchar not null," + //创建日期
                "color integer," + //颜色
                "finished integer," + //是否完成
                "progress real," + //进度
                "waveratio real," + //水波占比
                "ampratio real," +  //振幅比
                "totalpage integer," +  //总页数
                "currentpage integer," +   //当前已读
                "red integer," +  //红
                "green integer," +   //绿
                "blue integer)");  //蓝
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
