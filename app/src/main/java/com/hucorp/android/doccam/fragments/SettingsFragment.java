package com.hucorp.android.doccam.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.activities.PrivacyPolicyActivity;
import com.hucorp.android.doccam.activities.TermsConditionsActivity;

public class SettingsFragment extends Fragment {

    public static SettingsFragment newInstance()
    {
        return new SettingsFragment();
    }

    private CardView gdrive, dropbx, onedrive, youtube, twitch;
    private CardView gdrive_expand, dropbox_expand, onedrive_expand, youtube_expand, twitch_expand;
    private ImageView drive_arrow, dropbox_arrow, onedrive_arrow, youtube_arrow, twitch_arrow;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Settings");
        prefs = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        gdrive = v.findViewById(R.id.gdrive);
        dropbx = v.findViewById(R.id.dropbox);
        onedrive = v.findViewById(R.id.onedrive);
        youtube = v.findViewById(R.id.youtube);
        twitch = v.findViewById(R.id.twitch);
        gdrive_expand = v.findViewById(R.id.expand_gdrive);
        dropbox_expand = v.findViewById(R.id.expand_dropbox);
        onedrive_expand = v.findViewById(R.id.expand_onedrive);
        youtube_expand = v.findViewById(R.id.expand_youtube);
        twitch_expand = v.findViewById(R.id.expand_twitch);
        drive_arrow = v.findViewById(R.id.gdrive_arrow);
        dropbox_arrow = v.findViewById(R.id.dropbox_arrow);
        onedrive_arrow = v.findViewById(R.id.onedrive_arrow);
        youtube_arrow = v.findViewById(R.id.youtube_arrow);
        twitch_arrow = v.findViewById(R.id.twitch_arrow);


        gdrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(gdrive, gdrive_expand, drive_arrow);
            }
        });

        dropbx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(dropbx, dropbox_expand, dropbox_arrow);
            }
        });

        onedrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(onedrive, onedrive_expand, onedrive_arrow);
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(youtube, youtube_expand, youtube_arrow);
            }
        });

        twitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(twitch, twitch_expand, twitch_arrow);
            }
        });



        return v;
    }

    private void toggle(CardView card, CardView expandable, ImageView button){
        if(expandable.getVisibility() == View.GONE){
            TransitionManager.beginDelayedTransition(gdrive, new AutoTransition());
            expandable.setVisibility(View.VISIBLE);
            button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
        } else {
            TransitionManager.beginDelayedTransition(gdrive, new AutoTransition());
            expandable.setVisibility(View.GONE);
            button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
        }
    }
}
