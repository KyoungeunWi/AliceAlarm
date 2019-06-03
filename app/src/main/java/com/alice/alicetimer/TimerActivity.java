package com.alice.alicetimer;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.TimedText;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    public  String TAG = "TimerActivity : " ;
    public final static int REQEST_CODE_RINGTONE =  1000 ;

    private TextView mTimeSetTextView ;
    private TextView mRingtoneTextView ;
    private int mTime;
    private Uri mRingToneUri;
    private String mRingToneTitle ;
    private long mTimerId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        mTimeSetTextView = findViewById(R.id.time_setting_text);
        mRingtoneTextView = findViewById(R.id.sound_setting_text);

        /* If the intent has no Extra data , it is for inserting timer
        *  Or it is for Editing exist timer */
        Intent intent = getIntent();
        if(intent.getExtras() != null){
            mTimerId = intent.getLongExtra("ID",-1);
            mRingToneTitle = intent.getStringExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE);
            mTime = Integer.parseInt(String.valueOf(intent.getStringExtra(TimerContract.TimerEntry.COULUM_NAME_TIME)));
            mRingToneUri = Uri.parse(intent.getStringExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI));

            mRingtoneTextView.setText(mRingToneTitle);
            mTimeSetTextView.setText(mTime + " seconds");
        }
    }

    public void timeSetButtonClicked(View view) {
        TimeSettingDialog timeSettingDialog = new TimeSettingDialog();
        timeSettingDialog.setValueChangeListener(this);
        timeSettingDialog.show(getSupportFragmentManager() , "NumberPicker");
    }


    public void soundSetButtonClicked(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Select Ringtone");
        Uri uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, 1l);
        // Don't show 'Silent'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        startActivityForResult(intent, REQEST_CODE_RINGTONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQEST_CODE_RINGTONE && resultCode == RESULT_OK){
            mRingToneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (mRingToneUri != null) {
                Log.d(TAG, mRingToneUri.toString( ));
                Ringtone ringtone = RingtoneManager.getRingtone(this, mRingToneUri);
                mRingToneTitle = ringtone.getTitle(this);

                mRingtoneTextView.setText(mRingToneTitle);
            }
        }
    }

    public void saveButtonClicked(View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE, mRingToneTitle);
        contentValues.put(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI, String.valueOf(mRingToneUri) );
        contentValues.put(TimerContract.TimerEntry.COULUM_NAME_TIME , mTime);

        SQLiteDatabase db = TimerDbHelper.getsInstance(this).getWritableDatabase();
        /* if this is for inserting timer in the list , mTimerId == -1 */
        if(mTimerId == -1) {
            long newRowId = db.insert(TimerContract.TimerEntry.TABLE_NAME, null, contentValues);
            if (newRowId == -1) {
                Log.d(TAG, Long.toString(newRowId));
                Toast.makeText(this, "Timer setting fail", Toast.LENGTH_LONG).show( );
            } else {
                Toast.makeText(this, "Timer setting saved", Toast.LENGTH_LONG).show( );
                setResult(RESULT_OK);
                finish( );
            }
        }else {
            /* if this is for Editing a timer of the list  */
            int count = db.update(TimerContract.TimerEntry.TABLE_NAME,contentValues,
                    TimerContract.TimerEntry._ID + " = " + mTimerId , null);
            if(count == 0){
                Toast.makeText(this , "Timer editing fail" , Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,"Timer setting saved",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }

        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        mTimeSetTextView.setText(newVal + " seconds");

        mTime = newVal ;
    }
}
