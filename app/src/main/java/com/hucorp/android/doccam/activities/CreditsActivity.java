package com.hucorp.android.doccam.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hucorp.android.doccam.R;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        setTitle("Credits");
    }
}