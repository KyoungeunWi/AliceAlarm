package com.alice.alicetimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/* This class is First activity that user interact with app
*  It shows list of timers user saved and let them add timer with pink button.
*  Users also remove a timer items they don't use with long click */

public class MainActivity extends AppCompatActivity {
    public final int REQ_CODE_INSERT_TIMER = 1011 ;
    public final int REQ_CODE_EDIT_TIMER = 1012 ;

    private  TimerListAdaper mTimerListAdaper ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* registration of Broadcast Receiver action */
        TimerBroadcastReceiver myReceiver = new TimerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TimerBroadcastReceiver.ACTION_PLAY_SOUND);
        intentFilter.addAction(Intent.ACTION_CALL);
        registerReceiver(myReceiver,intentFilter);

        /* When Flotion action button's clicked , it will move to timer registration activity  */
        FloatingActionButton fab  = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, TimerActivity.class), REQ_CODE_INSERT_TIMER);
            }
        });

        ListView listView = findViewById(R.id.timerList);

        mTimerListAdaper = new TimerListAdaper(this , getCursor());
        listView.setAdapter(mTimerListAdaper);

        /* if the one item of list is clicked , It will move to editing activity */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener( ) {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) mTimerListAdaper.getItem(position);
                String ringtoneTitle = cursor.getString(cursor.getColumnIndexOrThrow(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE));
                String ringtoneURI = cursor.getString(cursor.getColumnIndexOrThrow(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI));
                String ringtonetime = cursor.getString(cursor.getColumnIndexOrThrow(TimerContract.TimerEntry.COULUM_NAME_TIME));

                Intent intent = new Intent(MainActivity.this , TimerActivity.class);
                intent.putExtra("ID" , id );
                intent.putExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE ,ringtoneTitle);
                intent.putExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI,ringtoneURI);
                intent.putExtra(TimerContract.TimerEntry.COULUM_NAME_TIME,ringtonetime);

                startActivityForResult(intent , REQ_CODE_EDIT_TIMER);
            }
        });

        /* if the one item of list is clicked for long time ,
         * It will show the dialog to delete timer item */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener( ) {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long deletedId =id;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete Timer");
                builder.setMessage("Do you want to delete this timer?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int deletedCount = getContentResolver().delete(TimerProvider.CONTENT_URI,
                                TimerContract.TimerEntry._ID + " = " + deletedId, null);
                        if(deletedCount == 0){
                            Toast.makeText(MainActivity.this ,"Deleting fail" , Toast.LENGTH_LONG).show();
                        }else{
                            mTimerListAdaper.swapCursor(getCursor());
                            Toast.makeText(MainActivity.this ,"Deleted !! " , Toast.LENGTH_LONG).show();
                        }

                    }
                });
                builder.setNegativeButton("CANCEL" , null);
                builder.show();
                return true;
            }
        });
    }


    /* This method is for getting timer items from DB */
    private Cursor getCursor(){
        return getContentResolver().query(TimerProvider.CONTENT_URI ,
                null ,null,null,
                 TimerContract.TimerEntry._ID + " DESC");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQ_CODE_INSERT_TIMER) {
            mTimerListAdaper.swapCursor(getCursor( ));
        }
        else if(resultCode == RESULT_OK && requestCode == REQ_CODE_EDIT_TIMER) {
            mTimerListAdaper.swapCursor(getCursor( ));
        }
    }
}
