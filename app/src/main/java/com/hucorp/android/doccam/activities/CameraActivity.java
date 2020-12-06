package com.hucorp.android.doccam.activities;

import androidx.fragment.app.Fragment;

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