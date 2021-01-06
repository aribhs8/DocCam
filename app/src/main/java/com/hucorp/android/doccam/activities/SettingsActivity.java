package com.hucorp.android.doccam.activities;

import androidx.fragment.app.Fragment;

import com.hucorp.android.doccam.fragments.SettingsFragment;
import com.hucorp.android.doccam.SingleFragmentActivity;

public class SettingsActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment() {
        setTitle("Settings");
        return SettingsFragment.newInstance();
    }
}
