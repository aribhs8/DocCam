package com.hucorp.android.doccam.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Data;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CdnSettings;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastContentDetails;
import com.google.api.services.youtube.model.LiveBroadcastSnippet;
import com.google.api.services.youtube.model.LiveBroadcastStatus;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamContentDetails;
import com.google.api.services.youtube.model.LiveStreamSnippet;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.activities.SettingsActivity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
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
                                /*
                                // Start live streaming
                                // Todo: Probably need to create new youtube object with Google Sign in info
                                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(),
                                        Collections.singleton(Constants.SCOPES[1]));

                                YouTube youtube = new YouTube.Builder(
                                        new NetHttpTransport(),
                                        new JacksonFactory(),
                                        credential)
                                        .setApplicationName(Constants.APP_TAG)
                                        .build();

                                // CREATE BROADCAST
                                // Get title for stream
                                String title = ((EditText) v.findViewById(R.id.title)).getText().toString();

                                // Create a snippet with title, scheduled start time
                                LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
                                broadcastSnippet.setTitle(title);
                                broadcastSnippet.setScheduledStartTime(new DateTime(new Date().toString()));

                                // Create LiveBroadCastStatus
                                LiveBroadcastStatus status = new LiveBroadcastStatus();
                                // Todo: Change status to be dependent on privacy settings
                                status.setPrivacyStatus("private");

                                LiveBroadcast broadcast = new LiveBroadcast();
                                broadcast.setKind("youtube#liveBroadcast");
                                broadcast.setSnippet(broadcastSnippet);

                                // Create insert request
                                try
                                {
                                    YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
                                            youtube.liveBroadcasts().insert("snippet,status", broadcast);
                                    // Request is executed and inserted broadcast is returned
                                    LiveBroadcast returnedBroadcast = liveBroadcastInsert.execute();

                                    // CREATE STREAM
                                    LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
                                    streamSnippet.setTitle(title);

                                    // Create content distribution network with framework and ingestion type
                                    CdnSettings cdn = new CdnSettings();
                                    // Todo: Get format from settings fragment
                                    cdn.setFormat("1080p");
                                    cdn.setIngestionType("rtmp");

                                    LiveStream stream = new LiveStream();
                                    stream.setKind("youtube#liveStream");
                                    stream.setSnippet(streamSnippet);
                                    stream.setCdn(cdn);

                                    YouTube.LiveStreams.Insert liveStreamInsert =
                                            youtube.liveStreams().insert("snippet,cdn", stream);
                                    LiveStream returnedStream = liveStreamInsert.execute();

                                    // Create the bind request
                                    YouTube.LiveBroadcasts.Bind liveBroadcastBind =
                                            youtube.liveBroadcasts().bind(returnedBroadcast.getId(), "id,contentDetails");

                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }


                                 */

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
