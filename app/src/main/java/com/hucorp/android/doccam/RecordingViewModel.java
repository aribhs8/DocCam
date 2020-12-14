package com.hucorp.android.doccam;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.FileProvider;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static androidx.core.content.ContextCompat.startActivity;

public class RecordingViewModel extends BaseObservable
{
    private Recording mRecording;
    private Context mContext;

    public RecordingViewModel(Context context)
    {
        mContext = context;
    }

    public Recording getRecording()
    {
        return mRecording;
    }

    @Bindable
    public String getTitle()
    {
        return mRecording.getTitle();
    }

    public void setRecording(Recording recording)
    {
        mRecording = recording;
        notifyChange();
    }

    @Bindable
    public String getDateFormatted()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        return formatter.format(mRecording.getDate());
    }

    @Bindable
    public String getTimeFormatted()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss", Locale.CANADA);
        return formatter.format(mRecording.getDate());
    }

    public void onClick()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(mContext, "com.hucorp.android.doccam.fileprovider", CameraLab.get(mContext).getRecordingFile(mRecording)), "video/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(mContext, intent, null);
    }
}
