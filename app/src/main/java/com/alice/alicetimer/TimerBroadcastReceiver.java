package com.alice.alicetimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class TimerBroadcastReceiver extends BroadcastReceiver {
    private static final String  TAG = "TimerBroadcastReceiver:" ;

    public static final String ACTION_PLAY_SOUND = "com.alice.alicetimer.ACTION_PLAY_MUSIC";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ACTION_PLAY_SOUND.equals(intent.getAction())){
            Uri ringToneUri = intent.getParcelableExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI);
            Toast.makeText(context , "Music starts" ,  Toast.LENGTH_LONG).show();
            if(ringToneUri != null) {
                Intent intentToService = new Intent(context, AliceService.class);
                intentToService.putExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI, ringToneUri);
                context.startService(intentToService);
            }
            else {
                Log.d(TAG, " onReceive: ringToneUri != null :" + ringToneUri.toString( ));
            }
        }
    }
}
