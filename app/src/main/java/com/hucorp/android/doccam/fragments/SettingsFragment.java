  package com.hucorp.android.doccam.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.activities.PrivacyPolicyActivity;
import com.hucorp.android.doccam.activities.SettingsActivity;
import com.hucorp.android.doccam.activities.TermsConditionsActivity;

import java.util.concurrent.Executor;

public class SettingsFragment extends Fragment {

    private static final int RC_SIGN_IN = 0 ;

    public static SettingsFragment newInstance()
    {
        return new SettingsFragment();
    }

    private CardView gdrive, dropbx, onedrive, youtube, twitch;
    private CardView gdrive_expand, dropbox_expand, onedrive_expand, youtube_expand, twitch_expand;
    private ImageView drive_arrow, dropbox_arrow, onedrive_arrow, youtube_arrow, twitch_arrow;
    private Button privacy_policy, terms_conditions, gdrive_authenticate;
    private TextView gdrive_text;
    private String personName, personGivenName, personFamilyName, personEmail, personId, personPhoto;

    //Google sign in
    GoogleSignInClient mGoogleSignInClient;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Settings");
        prefs = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = prefs.edit();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
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
        privacy_policy = v.findViewById(R.id.privacy_policy);
        terms_conditions = v.findViewById(R.id.term_conditions);
        gdrive_authenticate = v.findViewById(R.id.gdrive_authenticate);
        gdrive_text = v.findViewById(R.id.gdrive_text);

        GoogleSignIn();
        ButtonsOnClickListener();

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


    private void ButtonsOnClickListener(){
        gdrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
                if(acct != null){
                    toggle(gdrive, gdrive_expand, drive_arrow);
                } else {
                    GoogleHandleSignIn();
                }
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

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(i);
            }
        });

        terms_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TermsConditionsActivity.class);
                startActivity(i);
            }
        });

        gdrive_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
                if(acct == null){
                    GoogleHandleSignIn();
                } else {
                    signOut();
                }
            }
        });

    }

    private void GoogleHandleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            GoogleSignIn();
            Toast.makeText(getContext(), "Sign in successful", Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Succesfully signed out", Toast.LENGTH_SHORT).show();
                        gdrive_text.setText("Sync with Google Drive");
                        gdrive_expand.setVisibility(View.GONE);
                        drive_arrow.setVisibility(View.GONE);
                        drive_arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    }
                });
    }
    private void GoogleSignIn(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            personName = acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            personId = acct.getId();
            gdrive_authenticate.setText("Log out");
            gdrive_authenticate.setBackgroundColor(Color.parseColor("#FF0000"));
            gdrive_text.setText(personEmail);
            drive_arrow.setVisibility(View.VISIBLE);
        }
    }
}
