package com.hucorp.android.doccam.helper;

import android.content.Context;
import android.os.Handler;
import android.widget.Toolbar;

import com.hucorp.android.doccam.interfaces.TimerToolbarCallbacks;

import java.util.Locale;

public class Timer
{
    private static Timer sTimer;
    private Context mContext;

    // Timer control
    private Handler mDurationHandler;
    private int mSeconds;
    private String mTime;

    public static Timer get(Context context)
    {
        if (sTimer == null)
        {
            sTimer = new Timer(context);
        }
        return sTimer;
    }

    public Timer(Context context)
    {
        mContext = context.getApplicationContext();
        mDurationHandler = new Handler();
        mSeconds = 0;
    }

    public void durationStopWatch(TimerToolbarCallbacks callbacks)
    {
        mDurationHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mDurationHandler.postDelayed(this, 500);
                mSeconds++;
                if (mSeconds % 2 == 0)
                {
                    int hours = (mSeconds/2) / 3600;
                    int minutes = (mSeconds/2 % 3600) / 60;
                    int seconds = (mSeconds/2) % 60;
                    mTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                    callbacks.updateDurationText();
                }
                callbacks.updateIndicator(mSeconds);
            }
        }, 500);
    }

    public String getTimeElapsed()
    {
        return mTime;
    }

    public void resetTimer()
    {
        mSeconds = 0;
        mDurationHandler.removeCallbacksAndMessages(null);
    }
}
