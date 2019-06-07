package com.alice.alicetimer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/* This class is Sevice class that runs in background
   and plays ringtone user set until user click 'cancel' button */

public class AliceService extends Service {
    public static final String TAG = "AliceService :" ;

    MediaPlayer mMediaPlayer ;
    private boolean mMusicPlaying ;

    public AliceService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Uri ringToneUri = intent.getParcelableExtra(TimerContract.TimerEntry.COULUM_NAME_RINGTONE_URI);

        if (ringToneUri != null) {
            Log.d(TAG, "onStartCommand :" + ringToneUri.toString( ));
            mMediaPlayer = new MediaPlayer( );
            try {
                mMediaPlayer.setDataSource(this, ringToneUri);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare( );

            } catch (IOException e) {
                e.printStackTrace( );
            }
            mMediaPlayer.start( );
            mMusicPlaying = true ;
        }
        else {
            Log.d(TAG, "onStartCommand ringToneUri == null :");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy( );
        Log.d(TAG, " onDestroy");

        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMusicPlaying = false ;
    }

    public boolean ismMusicPlaying() {
        return mMusicPlaying;
    }

}
