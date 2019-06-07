package com.alice.alicetimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* This class is to handle DB */

public class TimerDbHelper extends SQLiteOpenHelper {

    private static TimerDbHelper sInstance ;

    /*Database version*/
    private static final int DB_VERSION = 1 ;

    /*Databaser file name */
    private static final String DB_NAME = "Timer.db";

    /*SQL to create table */
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT)",
                            TimerContract.TimerEntry.TABLE_NAME,
                            TimerContract.TimerEntry._ID,
                            TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE,
                            TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI,
                            TimerContract.TimerEntry.COULUM_NAME_TIME);

    /* SQL to delete table */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TimerContract.TimerEntry.TABLE_NAME ;

    private TimerDbHelper(Context context){
       super(context, DB_NAME ,null , DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public static synchronized TimerDbHelper getsInstance( Context context) {
        if(sInstance == null ){
            sInstance = new TimerDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }
}
