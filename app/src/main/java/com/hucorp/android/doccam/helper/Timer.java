package com.hucorp.android.doccam.helper;

import android.os.Handler;

import com.hucorp.android.doccam.interfaces.TimerToolbarCallbacks;

import java.util.Locale;

public class Timer
{
    private static int sSeconds;
    private static String sTime;
    private static final Handler sDurationHandler;
    private static TimerToolbarCallbacks sCallbacks;

    static {
        sSeconds = 0;
        sTime = "00:00";
        sDurationHandler = new Handler();
    }

    public static void setToolbarCallback(TimerToolbarCallbacks callbacks)
    {
        sCallbacks = callbacks;
    }

    public static void runHalfSecondTimer()
    {
        sDurationHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                sDurationHandler.postDelayed(this, 500);
                sSeconds++;
                if (sSeconds % 2 == 0)
                {
                    int minutes = (sSeconds/2 % 3600) / 60;
                    int secs = sSeconds/2 % 60;
                    sTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                    if (sCallbacks != null) sCallbacks.updateDurationText();
                }
                if (sCallbacks != null) sCallbacks.updateIndicator(sSeconds);
            }
        }, 500);
    }

    public static String getTimeElapsed()
    {
        return sTime;
    }

    public static void resetTimer()
    {
        sSeconds = 0;
        sDurationHandler.removeCallbacksAndMessages(null);
    }

}
