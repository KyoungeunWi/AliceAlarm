package com.alice.alicetimer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimerListAdaper extends CursorAdapter {
    public TimerListAdaper(Context context ,Cursor c){
        super(context, c ,false);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.timer_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView ringtoneTitle = view.findViewById(R.id.list_item_ringtone_title);
        ringtoneTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE)));

        TextView timeText = view.findViewById(R.id.list_item_time);
        timeText.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                TimerContract.TimerEntry.COULUM_NAME_TIME ))+ " seconds");
    }
}
