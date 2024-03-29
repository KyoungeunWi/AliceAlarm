package com.alice.alicetimer.contentProvider;

import android.provider.BaseColumns;


/* This class defines the colum name to make handling DB  easy */

public final class TimerContract {
    /* This is to prevent making this class instance */
    private TimerContract(){}

    public static class TimerEntry implements BaseColumns{
        public static final String TABLE_NAME = "timers" ;
        public static final String COULUM_NAME_RINGTONE_TITLE = "ringtitle";
        public static final String COULUM_NAME_RINGTONE_URI = "ringuri";
        public static final String COULUM_NAME_TIME = "second";
    }
}
