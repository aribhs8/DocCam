package com.hucorp.android.doccam.activities;

import androidx.fragment.app.Fragment;

import com.hucorp.android.doccam.SingleFragmentActivity;
import com.hucorp.android.doccam.fragments.RecordingListFragment;

public class RecordingListActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        setTitle("Recordings");
        return RecordingListFragment.newInstance();
    }
}
