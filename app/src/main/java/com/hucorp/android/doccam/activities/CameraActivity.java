package com.hucorp.android.doccam.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.fragments.CameraFragment;
import com.hucorp.android.doccam.SingleFragmentActivity;

public class CameraActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return CameraFragment.newInstance();
    }
}