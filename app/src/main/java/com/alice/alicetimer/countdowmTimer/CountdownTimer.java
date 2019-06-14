package com.alice.alicetimer.countdowmTimer;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alice.alicetimer.service.AliceService;
import com.alice.alicetimer.R;
import com.alice.alicetimer.contentProvider.TimerContract;

import static com.alice.alicetimer.broadcastReceiver.TimerBroadcastReceiver.ACTION_PLAY_SOUND;

/* This class is for showing countdown.
   And It contains subclass that performs Asynctask
*  to keep doing countdown even when activiy losing focus */

public class CountdownTimer extends AppCompatActivity {

    private int mTime_seconds = 0;
    private Uri mRingToneUri;
    private String mRingToneTitle;


    private boolean mBound;
    private TextView timeCountText;

    private CountdownAsycTask countdownTask;

    private Intent brodcastIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_timer);

        timeCountText = findViewById(R.id.timeCount);

        /*getting ringtone uri and time to do countdown and play music*/
        Intent intent = getIntent( );
        if (intent.getExtras( ) != null) {
            mRingToneTitle = intent.getStringExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_TITLE);
            mRingToneUri = Uri.parse(intent.getStringExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI));
            mTime_seconds = intent.getIntExtra(TimerContract.TimerEntry.COULUM_NAME_TIME, 0);

            if (mTime_seconds > 0) {
                countdownTask = new CountdownAsycTask( );
                countdownTask.execute(mTime_seconds);
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart( );
        finish();
    }

    /* After the countdown end, this avtivity sends broadcast receiver to play music*/
    public void cancleButtonClicked(View view) {
        if(brodcastIntent != null && brodcastIntent.getAction().equals(ACTION_PLAY_SOUND)) {
            Intent intentToService = new Intent(this, AliceService.class);
            stopService(intentToService);
            brodcastIntent = null ;
        }
        countdownTask.cancel(true);
        finish( );
    }


    /* This class is AsyncTask for countdown and sending broadcast to play music */
    private class CountdownAsycTask extends AsyncTask<Integer, Integer, Void> {
        static final String TAG = "CountdownAsycTask : ";

        @Override
        protected Void doInBackground(Integer... max) {

            for (int t = max[0]; t >= 0; t--) {
                Log.d(TAG, " max[0]=" + max[0] + " t =" + t);
                publishProgress(t);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace( );
                }

                if (isCancelled( )) {
                    break;
                }
            }
            return null;
        }

        /* showing current countdown number to screen */
        @Override
        protected void onProgressUpdate(Integer... values) {
            timeCountText.setText(Integer.toString(values[0]));
        }

        /* After the countdown end, this avtivity sends broadcast receiver to play music*/
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            brodcastIntent = new Intent(ACTION_PLAY_SOUND);
            brodcastIntent.putExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI,mRingToneUri);
            sendBroadcast(brodcastIntent);
        }

        /* when cancel button is clicked this timer will stop and move to back*/
        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext( ), "Timer is cancelled", Toast.LENGTH_LONG).show( );
            finish();
        }
    }
}