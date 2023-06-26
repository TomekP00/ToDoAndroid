package com.example.pum_todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 11;// If you change the database schema, you must increment the database version.
    public static final String DATABASE_NAME = "Database.db";
    private static final String SQL_CREATE_ENTRIES_TODO =
            "CREATE TABLE " + Todo.TodoEntry.TABLE_TODO + " (" +
                    Todo.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Todo.TodoEntry.COLUMN_TODO_TITLE + " TEXT," +
                    Todo.TodoEntry.COLUMN_TODO_DESC + " TEXT," +
                    Todo.TodoEntry.COLUMN_TODO_DUE_DATE + " TEXT," +
                    Todo.TodoEntry.COLUMN_TODO_DUE_TIME + " TEXT," +
                    Todo.TodoEntry.COLUMN_TODO_DONE + " INTEGER," +
                    Todo.TodoEntry.COLUMN_TODO_CREATED_AT + " TEXT," +
                    Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID + " INTEGER," +
                    "FOREIGN KEY (" + Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID + ") REFERENCES " +
                    Category.CategoryEntry.TABLE_CATEGORY + "(" + Category.CategoryEntry._ID + ")" +
                    ")";

    private static final String SQL_DELETE_ENTRIES_TODO =
            "DROP TABLE IF EXISTS " + Todo.TodoEntry.TABLE_TODO;

    private static final String SQL_CREATE_ENTRIES_CATEGORY =
            "CREATE TABLE " + Category.CategoryEntry.TABLE_CATEGORY + " (" +
                    Category.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Category.CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES_CATEGORY =
            "DROP TABLE IF EXISTS " + Category.CategoryEntry.TABLE_CATEGORY;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_CATEGORY);
        db.execSQL(SQL_CREATE_ENTRIES_TODO);

        ContentValues values = new ContentValues();
        values.put(Category.CategoryEntry.COLUMN_CATEGORY_NAME, "Domyślna");
        db.insert(Category.CategoryEntry.TABLE_CATEGORY, null, values);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_TODO);
        db.execSQL(SQL_DELETE_ENTRIES_CATEGORY);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
