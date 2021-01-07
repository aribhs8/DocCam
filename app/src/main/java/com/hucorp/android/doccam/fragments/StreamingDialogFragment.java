package com.hucorp.android.doccam.fragments;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.api.Scope;
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

                // Add validation for stream title
                EditText editStreamTitle = (EditText) v.findViewById(R.id.title);
                editStreamTitle.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        if (editStreamTitle.getText().length() == 0)
                        {
                            // Validate that field is filled
                            editStreamTitle.setError("Title is required!");
                            ((androidx.appcompat.app.AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            ((androidx.appcompat.app.AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {

                    }
                });

                // Set dropdown for privacy
                AutoCompleteTextView autoCompleteTextView = v.findViewById(R.id.autoCompleteText);
                String[] privacyOptions = {"Public", "Private"};
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.option_item, privacyOptions);
                autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(), false);
                autoCompleteTextView.setAdapter(arrayAdapter);

                // Build dialog
                builder.setMessage(String.format(Locale.getDefault(), "Are you sure you want to start a live streaming session on %s?", getArguments().getSerializable(ARG_SERVICE)))
                        .setTitle("Going Live...")
                        .setView(v)
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                /*
                                // Setup Live Stream
                                // Get YouTube info
                                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(), Collections.singleton(Constants.SCOPES[1]));
                                credential.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(getActivity()).getAccount());
                                YouTube youtube = new YouTube(new NetHttpTransport(), new JacksonFactory(), credential);

                                // Create broadcast
                                LiveBroadcast broadcast = generateBroadcast(generateBroadcastSnippet(editStreamTitle.getText().toString()));
                                broadcast.setStatus(new LiveBroadcastStatus().setPrivacyStatus("private"));

                                try
                                {
                                    // Get inserted broadcast
                                    LiveBroadcast insertedBroadcast = generateInsertRequest(youtube, broadcast);

                                    // Create stream and bind
                                    LiveStream stream = generateStream(new LiveStreamSnippet().setTitle(editStreamTitle.getText().toString()), getCdnSettings());
                                    LiveStream insertedStream = generateInsertRequest(youtube, stream);
                                    youtube.liveBroadcasts().bind(insertedBroadcast.getId(), "id, contentDetails");

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

    /*==================================================
     *           YOUTUBE LIVE STREAM SETUP
     * ==================================================*/

    private LiveBroadcastSnippet generateBroadcastSnippet(String title)
    {
        LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
        broadcastSnippet.setTitle(title);
        broadcastSnippet.setScheduledStartTime(new DateTime(new Date().toString()));

        return broadcastSnippet;
    }

    private LiveBroadcast generateBroadcast(LiveBroadcastSnippet broadcastSnippet)
    {
        LiveBroadcast broadcast = new LiveBroadcast();
        broadcast.setKind("youtube#liveBroadcast");
        broadcast.setSnippet(broadcastSnippet);

        return broadcast;
    }

    private LiveBroadcast generateInsertRequest(YouTube youtube, LiveBroadcast broadcast) throws IOException
    {
        YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
                youtube.liveBroadcasts().insert("snippet,status", broadcast);

        // Request is executed and inserted broadcast is returned
        return liveBroadcastInsert.execute();
    }

    private CdnSettings getCdnSettings()
    {
        CdnSettings cdn = new CdnSettings();
        // Todo: Get format from Settings
        cdn.setFormat("1080p");
        cdn.setIngestionType("rtmp");
        return cdn;
    }

    private LiveStream generateStream(LiveStreamSnippet streamSnippet, CdnSettings cdn)
    {
        LiveStream stream = new LiveStream();
        stream.setKind("youtube#liveStream");
        stream.setSnippet(streamSnippet);
        stream.setCdn(cdn);

        return stream;
    }

    private LiveStream generateInsertRequest(YouTube youtube, LiveStream stream) throws IOException
    {
        YouTube.LiveStreams.Insert liveStreamInsert =
                youtube.liveStreams().insert("snippet,cdn", stream);

        return liveStreamInsert.execute();
    }
}
