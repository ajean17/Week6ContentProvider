package com.example.alvin.w5_d3_ex01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.alvin.w5_d3_ex01.FeedReaderContract.MovieEntry;
import com.example.alvin.w5_d3_ex01.FeedReaderContract.GenreEntry;

public class DBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "films.db";//db extension is crucial

    public static final String SQL_CREATE_MOVIES = "CREATE TABLE " +
            MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieEntry.COLUMN_NAME + " TEXT," +
            MovieEntry.COLUMN_DATE + " TEXT," +
            MovieEntry.COLUMN_GENRE + " INTEGER, FOREIGN KEY("+
            MovieEntry.COLUMN_GENRE + ") REFERENCES "+
            GenreEntry.TABLE_NAME + "(" + GenreEntry._ID +"));";


    public static final String SQL_CREATE_GENRES = "CREATE TABLE " +
            GenreEntry.TABLE_NAME + " (" +
            GenreEntry._ID + " INTEGER PRIMARY KEY," +
            GenreEntry.COLUMN_NAME + " TEXT)";

    public static final String SQL_DELETE_GENRES = "DROP TABLE IF EXISTS " + GenreEntry.TABLE_NAME;
    public static final String SQL_DELETE_MOVIES = "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GENRES);
        db.execSQL(SQL_CREATE_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_GENRES);
        db.execSQL(SQL_DELETE_MOVIES);
        onCreate(db);
    }
}
