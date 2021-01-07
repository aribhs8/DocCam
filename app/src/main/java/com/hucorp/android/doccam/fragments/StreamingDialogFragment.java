package com.hucorp.android.doccam.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.activities.SettingsActivity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Locale;

public class StreamingDialogFragment extends DialogFragment
{
    private static final String ARG_SERVICE = "service";

    public static StreamingDialogFragment newInstance(String service)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SERVICE, service);

        StreamingDialogFragment fragment = new StreamingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        if (getArguments().getSerializable(ARG_SERVICE) == "YouTube")
        {
            if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null)
            {
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View v = inflater.inflate(R.layout.fragment_dialog_streaming, null);

                builder.setMessage(String.format(Locale.getDefault(), "Are you sure you want to start a live streaming session on %s?", getArguments().getSerializable(ARG_SERVICE)))
                        .setTitle("Going Live...")
                        .setView(v)
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // Start live streaming
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                AutoCompleteTextView autoCompleteTextView = v.findViewById(R.id.autoCompleteText);
                String[] privacyOptions = {"Public", "Private"};
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.option_item, privacyOptions);
                autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(), false);
                autoCompleteTextView.setAdapter(arrayAdapter);
            } else
            {
                builder.setMessage(String.format(Locale.getDefault(), "To stream to %s, you must provide your account details in settings", getArguments().getSerializable(ARG_SERVICE)))
                        .setTitle("Not Signed In")
                        .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                startActivity(new Intent(getContext(), SettingsActivity.class));
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
            }
        } else
        {
            builder.setTitle("Coming Soon")
                    .setMessage("This feature isn't available yet, but will be added in a future update")
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
        }

        return builder.create();
    }
}
