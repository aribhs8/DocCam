package com.hucorp.android.doccam.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hucorp.android.doccam.activities.SettingsActivity;

import java.util.Locale;

public class StreamingDialogFragment extends DialogFragment
{
    private static final String ARG_STREAM = "stream";
    private static final String ARG_SERVICE = "service";

    public static StreamingDialogFragment newInstance(boolean stream, String service)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_STREAM, stream);
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
        if ((boolean) getArguments().getSerializable(ARG_STREAM))
        {
            builder.setMessage(String.format(Locale.getDefault(), "Are you sure you want to start a live streaming session on %s?", getArguments().getSerializable(ARG_SERVICE)))
                    .setTitle("Going Live...")
                    .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
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
                    });
        }

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
