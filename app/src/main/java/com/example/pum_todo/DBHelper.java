package com.example.pum_todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;// If you change the database schema, you must increment the database version.
    public static final String DATABASE_NAME = "Todo.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Todo.TodoEntry.TABLE_NAME + " (" +
                    Todo.TodoEntry._ID + " INTEGER PRIMARY KEY," +
                    Todo.TodoEntry.COLUMN_NAME_TITLE + " TEXT," +
                    Todo.TodoEntry.COLUMN_NAME_DESC + " TEXT," +
                    Todo.TodoEntry.COLUMN_NAME_DONE + " BOOLEAN)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Todo.TodoEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
