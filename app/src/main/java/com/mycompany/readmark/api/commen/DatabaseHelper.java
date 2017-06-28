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
        db.execSQL("create table if not exists bookshelf("
                + "id integer primary key,"
                + "bookCount integer,"
                + "title varchar not null,"
                + "remark varchar,"
                + "orders integer,"
                + "create_at varchar not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
