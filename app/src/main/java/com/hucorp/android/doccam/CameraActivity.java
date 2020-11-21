package com.hucorp.android.doccam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class CameraActivity extends SingleFragmentActivity
{

    @Override
    protected Fragment createFragment()
    {
        return CameraFragment.newInstance();
    }
}