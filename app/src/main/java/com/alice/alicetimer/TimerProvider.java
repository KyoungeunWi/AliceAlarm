package com.alice.alicetimer;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Switch;

public class TimerProvider extends ContentProvider {
    /* provider name */
    private static final String AUTHORITY = "com.alice.alicetimer.provider";

    /* Table name of alice timer */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"
            + TimerContract.TimerEntry.TABLE_NAME);

    /*MIME for one item*/
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.com.alice.alicetimer.provider."
            + TimerContract.TimerEntry.TABLE_NAME ;

    /*MIME for many items*/
    public static final String CONTENT_ALL_TYPE = "vnd.android.cursor.dir/" +
            "vnd.com.alice.alicetimer.provider."
            + TimerContract.TimerEntry.TABLE_NAME ;

    /* Creating Uri matcher object*/
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int ALL = 1 ;
    public static final int ITEM = 2 ;

    static{
        sUriMatcher.addURI(AUTHORITY, TimerContract.TimerEntry.TABLE_NAME,ALL);
        sUriMatcher.addURI(AUTHORITY, TimerContract.TimerEntry.TABLE_NAME + "/#" ,ITEM);
    }
    private TimerDbHelper mTimerDbHelper;

    public TimerProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        switch(sUriMatcher.match(uri)){
            case ALL :
                break ;
            case ITEM:
                selection = "_id=" + ContentUris.parseId(uri);
                selectionArgs = null;
                break;
            case UriMatcher.NO_MATCH:
                return 0;
        }
        SQLiteDatabase db = mTimerDbHelper.getWritableDatabase();
        int deleteCount = db.delete(TimerContract.TimerEntry.TABLE_NAME,selection,selectionArgs);
        return deleteCount;
    }

    @Override
    public String getType(Uri uri) {
        /*handle requests for the MIME type of the data
        at the given URI.*/
        switch(sUriMatcher.match(uri)){
            case ALL :
                return CONTENT_ALL_TYPE ;
            case ITEM:
                return  CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }


    // requests to insert a new row.
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        switch (sUriMatcher.match(uri)){
            case ALL:
                long id = mTimerDbHelper.getWritableDatabase()
                        .insert(TimerContract.TimerEntry.TABLE_NAME,null,values);
                if(id>0){
                    Uri returnUri= ContentUris.withAppendedId(CONTENT_URI,id);
                    return returnUri;
                }
                break;
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        // initialize your content provider on startup.
        mTimerDbHelper = TimerDbHelper.getsInstance(getContext());
        return true ;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // handle query requests from clients.
        switch(sUriMatcher.match(uri)){
            case ALL :
                break;
            case ITEM:
                selection = "_id=" + ContentUris.parseId(uri);
                selectionArgs = null ;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }
        SQLiteDatabase db = mTimerDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TimerContract.TimerEntry.TABLE_NAME ,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // handle requests to update one or more rows.
        switch(sUriMatcher.match(uri)){
            case ALL:
                break;
            case ITEM:
                break;
            case UriMatcher.NO_MATCH:
                return 0;
        }
        SQLiteDatabase db = mTimerDbHelper.getWritableDatabase();
        int update = db.update(TimerContract.TimerEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        return update;
    }
}
