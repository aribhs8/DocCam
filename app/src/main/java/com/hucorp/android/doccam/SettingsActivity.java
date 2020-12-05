package com.hucorp.android.doccam;

import androidx.fragment.app.Fragment;

public class SettingsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return SettingsFragment.newInstance();
    }
}
