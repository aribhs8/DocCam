package com.hucorp.android.doccam;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CameraLab
{
    private static CameraLab sCameraLab;

    private List<Recording> mRecordings;

    private Context mContext;

    public static CameraLab get(Context context) {
        if (sCameraLab == null) {
            sCameraLab = new CameraLab(context);
        }
        return sCameraLab;
    }

    private CameraLab(Context context) {
        mContext = context.getApplicationContext();
        mRecordings = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Recording recording = new Recording();
            recording.setTitle("Recording #" + i);
            mRecordings.add(recording);
        }
    }

    public List<Recording> getRecordings()
    {
        return mRecordings;
    }

    public Recording getRecording(UUID id)
    {
        for (Recording recording : mRecordings)
        {
            if (recording.getID().equals(id))
            {
                return recording;
            }
        }

        return null;
    }
}
