package com.alice.alicetimer.countdowmTimer;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alice.alicetimer.R;
import com.alice.alicetimer.contentProvider.TimerContract;
import com.alice.alicetimer.contentProvider.TimerProvider;

public class TimerActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    public  String TAG = "TimerActivity : " ;
    public final static int REQEST_CODE_RINGTONE =  2000 ;
    public final static int REQEST_CODE_TIMER =  2100 ;

    private TextView mTimeSetTextView ;
    private TextView mRingtoneTextView ;
    private Switch mSwitchButton ;

    private int mTime_seconds;
    private Uri mRingToneUri;
    private String mRingToneTitle ;

    private long mTimerId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        mTimeSetTextView = findViewById(R.id.time_setting_text);
        mRingtoneTextView = findViewById(R.id.sound_setting_text);
        mSwitchButton = findViewById(R.id.start_switch);


       /* Initializing */
        final Intent intent = getIntent();
        initializeSetting(intent);


        /* When Switch is on , the countdown timer will start */
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener( ) {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent countdownIntent = new Intent(TimerActivity.this , CountdownTimer.class);
                countdownIntent.putExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE , mRingToneTitle);
                countdownIntent.putExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI , mRingToneUri.toString());
                countdownIntent.putExtra(TimerContract.TimerEntry.COULUM_NAME_TIME , mTime_seconds);

                Toast.makeText(getApplicationContext() , "Timer switch is on , Timer stars !!" , Toast.LENGTH_LONG).show();

                startActivityForResult(countdownIntent ,REQEST_CODE_TIMER );
                mSwitchButton.setChecked(false);
            }
        });
    }

    /* This part is to show dialog for time picking  */
    public void timeSetButtonClicked(View view) {
        TimeSettingDialog timeSettingDialog = new TimeSettingDialog();
        timeSettingDialog.setValueChangeListener(this);
        timeSettingDialog.show(getSupportFragmentManager() , "NumberPicker");
    }

    /* This part is for when time picking end from the number picking dialog */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        mTimeSetTextView.setText(newVal + " seconds");
        mTime_seconds = newVal ;
    }

    /*  This part is to show dialog for Ringtone picking and calling   */
    public void soundSetButtonClicked(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Select Ringtone");
        Uri uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, 1l);
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

    /* This method is saving a timer data (ringtone ,time) user set on storage  */
    public void saveButtonClicked(View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE, mRingToneTitle);
        contentValues.put(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI, String.valueOf(mRingToneUri) );
        contentValues.put(TimerContract.TimerEntry.COULUM_NAME_TIME , mTime_seconds);

        if(mTimerId == -1) {
            Uri uri = getContentResolver().insert(TimerProvider.CONTENT_URI , contentValues);
            if (uri == null) {
                Toast.makeText(this, "Timer setting fail", Toast.LENGTH_LONG).show( );
            } else {
                Toast.makeText(this, "Timer setting saved", Toast.LENGTH_LONG).show( );
                setResult(RESULT_OK);
                finish( );
            }
        }
        else
        {
            int count = getContentResolver().update(TimerProvider.CONTENT_URI ,
                    contentValues ,
                    TimerContract.TimerEntry._ID + " = " + mTimerId ,
                    null);
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

    /* When cancel button's clicked appplication shows mainActivity (list of timer items) */
    public void cancelButtonClicked(View view) {
        onBackPressed();
    }


    /*If the intent has no Extra data , it means inserting timer
    Or if the intent has Extra data it is for Editing exist timer */
    private void initializeSetting(Intent intent){
        if(intent.getExtras() != null){
            mTimerId = intent.getLongExtra("ID",-1);
            mRingToneTitle = intent.getStringExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE);
            mTime_seconds = Integer.parseInt(String.valueOf(intent.getStringExtra(TimerContract.TimerEntry.COULUM_NAME_TIME)));
            mRingToneUri = Uri.parse(intent.getStringExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI));
        }
        else {
            mRingToneUri = RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_RINGTONE);
            Ringtone ringtone = RingtoneManager.getRingtone(this, mRingToneUri);
            mRingToneTitle = ringtone.getTitle(getApplicationContext());
            mTime_seconds = 1 ;
        }

        mRingtoneTextView.setText(mRingToneTitle);
        mTimeSetTextView.setText(mTime_seconds + " seconds");
    }
}
