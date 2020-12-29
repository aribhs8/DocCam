package com.hucorp.android.doccam.helper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.hucorp.android.doccam.fragments.CameraFragment;
import com.hucorp.android.doccam.interfaces.CameraBarCallbacks;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.interfaces.TimerToolbarCallbacks;

import org.w3c.dom.Text;

public class CameraBar extends Toolbar implements View.OnClickListener, TimerToolbarCallbacks
{

    //Animations
    Animation fadein = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
    Animation fadeout = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);

    //Timer
    private boolean mDisplayTimerOptions = false;
    public static boolean fivetimer = false;
    public static boolean tentimer = false;

    private CameraBarCallbacks mCallbacks;

    // Control layout elements
    private View mLayout;
    private Button mFiveTimerBtn;
    private Button mTenTimerBtn;

    private TextView mTimeView;

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

        mTimeView = (TextView) mLayout.findViewById(R.id.time_display);

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

                 mFiveTimerBtn.setVisibility(getRootView().GONE);
                 mTenTimerBtn.setVisibility(getRootView().GONE);

                 mTimeView.setVisibility(getRootView().VISIBLE);
                 mTimeView.setText("");

            }
        }
    }

    public void updateDuration()
    {
        ((TextView) mLayout.findViewById(R.id.stopwatch)).setText(R.string.recording_duration);
        Timer.get(getContext()).durationStopWatch(this);
    }

    @Override
    public void updateDurationText()
    {
        ((TextView) mLayout.findViewById(R.id.stopwatch)).setText(Timer.get(getContext()).getTimeElapsed());
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
            if (v.getId() == R.id.action_settings && !CameraFragment.startedTimer)
            {
                mCallbacks.onSettingsClick();
            }

            else if (v.getId() == R.id.action_timer && !CameraFragment.startedTimer)
            {
                mDisplayTimerOptions = !mDisplayTimerOptions;

                if (mDisplayTimerOptions){
                    mFiveTimerBtn.startAnimation(fadein);
                    mTenTimerBtn.startAnimation(fadein);
                }

                else{
                    mFiveTimerBtn.startAnimation(fadeout);
                    mTenTimerBtn.startAnimation(fadeout);
                }

                mFiveTimerBtn.setVisibility(mDisplayTimerOptions ? View.VISIBLE : View.GONE);
                mTenTimerBtn.setVisibility(mDisplayTimerOptions ? View.VISIBLE : View.GONE);
            }

            else if (v.getId() == R.id.fivetimer)
            {

                mFiveTimerBtn.startAnimation(fadeout);
                mTenTimerBtn.startAnimation(fadeout);

                mDisplayTimerOptions = !mDisplayTimerOptions;

                mFiveTimerBtn.setVisibility(getRootView().GONE);
                mTenTimerBtn.setVisibility(getRootView().GONE);

                if (fivetimer) {

                    fivetimer = false;
                    mTimeView.setText("");
                }

                else{
                    fivetimer = true;
                    mTimeView.setText("5s");

                }

            }

            else if (v.getId() == R.id.tentimer)
            {

                mFiveTimerBtn.startAnimation(fadeout);
                mTenTimerBtn.startAnimation(fadeout);

                mDisplayTimerOptions = !mDisplayTimerOptions;

                mFiveTimerBtn.setVisibility(getRootView().GONE);
                mTenTimerBtn.setVisibility(getRootView().GONE);

                if (tentimer) {
                    tentimer = false;
                    mTimeView.setText("");

                }

                else {
                    tentimer = true;
                    mTimeView.setText("10s");
                }


            }

        }
    }
}