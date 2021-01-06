package com.hucorp.android.doccam.fragments;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.activities.CreditsActivity;
import com.hucorp.android.doccam.activities.PrivacyPolicyActivity;
import com.hucorp.android.doccam.activities.TermsConditionsActivity;
import com.hucorp.android.doccam.helper.CameraLab;

import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment
{
    private TextView mNumberOfRecordingsText;
    private CardView mDeleteAllRecordingsCard;
    private CardView mPrivacyPolicyCard;
    private CardView mTermsAndConditionsCard;
    private CardView mCreditsCard;
    private TextView mUserName;
    private Button mAccountBtn;
    private ImageView mProfilePic;
    private SwitchMaterial mUploadSwitch;

    // YouTube
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;


    public static SettingsFragment newInstance()
    {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Todo: access youtube account with this (does requestScope work for this?)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(Constants.SCOPES[0]), new Scope(Constants.SCOPES[1]))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        mNumberOfRecordingsText = (TextView) v.findViewById(R.id.number_of_recordings);
        mDeleteAllRecordingsCard = (CardView) v.findViewById(R.id.delete_all_recordings);
        mPrivacyPolicyCard = (CardView) v.findViewById(R.id.privacy_policy);
        mTermsAndConditionsCard = (CardView) v.findViewById(R.id.terms_and_conditions);
        mCreditsCard = (CardView) v.findViewById(R.id.Credits);
        mAccountBtn = (Button) v.findViewById(R.id.youtube_account_btn);
        mUserName = (TextView) v.findViewById(R.id.youtube_name);
        mProfilePic = (ImageView) v.findViewById(R.id.youtube_profile_pic);
        mUploadSwitch = (SwitchMaterial) v.findViewById(R.id.upload_after_stream_switch);

        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        updateUI();

        return v;
    }

    private void updateUI()
    {
        mNumberOfRecordingsText.setText(String.valueOf(CameraLab.get(getActivity()).getNumberOfRecordings()));

        // Todo: Update YouTube username to be one signed in
        if (mGoogleSignInAccount != null)
        {
            mUserName.setText(mGoogleSignInAccount.getDisplayName());
            mAccountBtn.setText(R.string.sign_out);
            Glide.with(getActivity()).load(mGoogleSignInAccount.getPhotoUrl()).into(mProfilePic);
            mUploadSwitch.setEnabled(true);
        } else
        {
            mUserName.setText(R.string.signed_in_text);
            mAccountBtn.setText(R.string.sign_in);
            mProfilePic.setImageResource(R.drawable.ic_profile_user);
            mUploadSwitch.setEnabled(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mDeleteAllRecordingsCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Delete All Recordings")
                        .setMessage("Warning: This will delete all recordings. Are you sure you want to do this?")
                        .setPositiveButton("Delete All", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // Delete all recordings
                                int numberOfRecordings = CameraLab.get(getActivity()).getNumberOfRecordings();
                                CameraLab.get(getActivity()).wipeAllData();
                                dialog.dismiss();
                                Snackbar.make(view, String.format(
                                        Locale.getDefault(), "%d recording%s deleted", numberOfRecordings, numberOfRecordings > 1 ? "s" : ""),
                                        Snackbar.LENGTH_SHORT).show();
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        mAccountBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mGoogleSignInAccount == null)
                {
                    // Sign into account
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, Constants.REQUEST_ACCOUNT_PICKER);
                } else
                {
                    // Sign out of account
                    signOut();
                }
            }
        });

        mPrivacyPolicyCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
            }
        });

        mTermsAndConditionsCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), TermsConditionsActivity.class));
            }
        });

        mCreditsCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), CreditsActivity.class));
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Constants.REQUEST_ACCOUNT_PICKER) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mGoogleSignInAccount = completedTask.getResult(ApiException.class);
//            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(), Collections.singleton(Constants.SCOPES[0]));
//            mService = new YouTube.Builder(
//                    new NetHttpTransport(),
//                    new JacksonFactory(),
//                    credential)
//                    .setApplicationName(Constants.APP_TAG)
//                    .build();

            updateUI();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(Constants.APP_TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signOut()
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {

                    }
                });

        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        mGoogleSignInAccount = null;
                        updateUI();
                    }
                });
    }
}