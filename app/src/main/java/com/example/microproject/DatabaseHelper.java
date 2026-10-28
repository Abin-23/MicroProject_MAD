package com.example.microproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "StudentPlanner.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE classes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subject TEXT NOT NULL, " +
                "day TEXT NOT NULL, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "room TEXT)");

        db.execSQL("CREATE TABLE routines (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "done INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE activities (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "notes TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS classes");
        db.execSQL("DROP TABLE IF EXISTS routines");
        db.execSQL("DROP TABLE IF EXISTS activities");
        onCreate(db);
    }

    public long insert(String table, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(table, null, values);
    }

    public Cursor getAll(String table) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(table, null, null, null,
                null, null, "id DESC");
    }

    public int delete(String table, int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(table, "id=?",
                new String[]{String.valueOf(id)});
    }

    public int updateRoutineStatus(int id, boolean done) {
        ContentValues values = new ContentValues();
        values.put("done", done ? 1 : 0);

        return getWritableDatabase().update(
                "routines", values, "id=?",
                new String[]{String.valueOf(id)});
    }
}
