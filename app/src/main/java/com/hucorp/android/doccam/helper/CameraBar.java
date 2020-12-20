package com.hucorp.android.doccam.helper;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hucorp.android.doccam.CameraBarCallbacks;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;

// Todo: Find efficient way to load onClickListeners
public class CameraBar extends Toolbar implements View.OnClickListener
{
    private CameraBarCallbacks mCallbacks;

    // Control layout elements
    private View mLayout;

    // Default layout elements
    private ImageButton mSettingsBtn;
    private ImageButton mFlashBtn;
    private ImageButton mTimerBtn;

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
    }

    @Override
    public void onClick(View v)
    {
        ImageButton b = (ImageButton) v;
        if (mCallbacks != null)
        {
            if (b.getId() == R.id.action_settings)
            {
                mCallbacks.onSettingsClick();
            }
        }
    }
}

/*
public class AppBar extends Toolbar implements View.OnClickListener
{
    private Toolbar mToolbar;
    private View mView;

    private ImageButton mSettingsBtn;
    private ImageButton mFlashBtn;

    public AppBar(@NonNull Context context, View view)
    {
        super(context);
        mView = view;
        this.switchToDefaultMode();
    }

    public AppBar(Context context)
    {
        super(context);
    }

    public void switchToDefaultMode()
    {
        mToolbar = mView.findViewById(R.id.camera_default_appbar);

        mSettingsBtn = mView.findViewById(R.id.action_settings);
        mFlashBtn = mView.findViewById(R.id.action_flash);

        this.processDefaultInput();
    }

    private void processDefaultInput()
    {
        mSettingsBtn.setOnClickListener(this);
        mFlashBtn.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v)
    {
        ImageButton b = (ImageButton) v;
        switch (b.getId())
        {
            case R.id.action_settings:
                Toast.makeText(getContext(), "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_flash:
                Toast.makeText(getContext(), "Flash", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

 */