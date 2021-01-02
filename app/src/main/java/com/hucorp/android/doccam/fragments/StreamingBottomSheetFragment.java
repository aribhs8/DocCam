package com.hucorp.android.doccam.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hucorp.android.doccam.R;

public class StreamingBottomSheetFragment extends BottomSheetDialogFragment
{
    private TextView mYouTube;
    private TextView mTwitch;

    public static StreamingBottomSheetFragment newInstance()
    {
        Bundle args = new Bundle();

        StreamingBottomSheetFragment fragment = new StreamingBottomSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.streaming_bottom_sheet, container, false);

        mYouTube = (TextView) v.findViewById(R.id.youtube_stream);
        mTwitch = (TextView) v.findViewById(R.id.twitch_stream);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
    }

    private void setListeners()
    {
        mYouTube.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismissAllowingStateLoss();
                Toast.makeText(getContext(), "YouTube button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        mTwitch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismissAllowingStateLoss();
                Toast.makeText(getContext(), "Twitch button pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
