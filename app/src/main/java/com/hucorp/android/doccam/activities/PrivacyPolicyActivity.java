package com.hucorp.android.doccam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hucorp.android.doccam.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        setTitle("Privacy Policy");
    }
}