package com.hucorp.android.doccam.helper;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.hucorp.android.doccam.interfaces.CameraBarCallbacks;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.interfaces.TimerToolbarCallbacks;

public class CameraBar extends Toolbar implements View.OnClickListener, TimerToolbarCallbacks
{
    //Timer
    private boolean mDisplayTimerOptions = false;

    private CameraBarCallbacks mCallbacks;

    // Control layout elements
    private View mLayout;
    private Button mFiveTimerBtn;
    private Button mTenTimerBtn;

    public static CameraBar newInstance(Context context)
    {
        return new CameraBar(context);
    }

    private CameraBar(@NonNull Context context)
    {
        super(context);
    }

    public void setCallback(CameraBarCallbacks callback)
    {
        mCallbacks = callback;
    }

    public void setLayout(View layout)
    {
        if (mLayout != null) mLayout.setVisibility(View.GONE);
        mLayout = layout;

        mLayout.setVisibility(View.VISIBLE);
        mFiveTimerBtn = (Button) mLayout.findViewById(R.id.fivetimer);
        mTenTimerBtn = (Button) mLayout.findViewById(R.id.tentimer);
        this.setListeners();
    }

    private void setListeners()
    {
        if (mLayout != null)
        {
            if (mLayout.getId() == R.id.default_camera_toolbar)
            {
                ((ImageButton) mLayout.findViewById(R.id.action_settings)).setOnClickListener(this);

                //Countdown timer
                ((ImageButton) mLayout.findViewById(R.id.action_timer)).setOnClickListener(this);
                 mFiveTimerBtn.setOnClickListener(this);
                 mTenTimerBtn.setOnClickListener(this);
            }
        }
    }

    public void updateDuration()
    {
        ((TextView) mLayout.findViewById(R.id.stopwatch)).setText(R.string.recording_duration);
        //Timer.setToolbarCallback(this);
        //Timer.runHalfSecondTimer();
    }

    @Override
    public void updateDurationText()
    {
        ((TextView) mLayout.findViewById(R.id.stopwatch)).setText(Timer.getTimeElapsed());
    }

    @Override
    public void updateIndicator(int seconds)
    {
        mLayout.findViewById(R.id.recording_indicator).setVisibility(seconds % 2 == 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onClick(View v)
    {
        if (mCallbacks != null)
        {
            if (v.getId() == R.id.action_settings)
            {
                mCallbacks.onSettingsClick();
            } else if (v.getId() == R.id.action_timer)
            {
                mDisplayTimerOptions = !mDisplayTimerOptions;
                mFiveTimerBtn.setVisibility(mDisplayTimerOptions ? View.VISIBLE : View.GONE);
                mTenTimerBtn.setVisibility(mDisplayTimerOptions ? View.VISIBLE : View.GONE);
            } else if (v.getId() == R.id.fivetimer)
            {

            } else if (v.getId() == R.id.tentimer)
            {

            }
        }
    }
}