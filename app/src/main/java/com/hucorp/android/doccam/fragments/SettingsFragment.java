package com.hucorp.android.doccam.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.activities.PrivacyPolicyActivity;
import com.hucorp.android.doccam.activities.TermsConditionsActivity;

public class SettingsFragment extends Fragment {

    public static SettingsFragment newInstance()
    {
        return new SettingsFragment();
    }
    private Button BtnPrivacy;
    private Button BtnTerms;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Settings");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        BtnPrivacy = v.findViewById(R.id.privacy_policy);
        BtnTerms = v.findViewById(R.id.terms_conditions);

        BtnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(i);
            }
        });

        BtnTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TermsConditionsActivity.class);
                startActivity(i);
            }
        });


        return v;
    }
}
