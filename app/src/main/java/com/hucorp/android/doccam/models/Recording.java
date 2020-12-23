package com.hucorp.android.doccam.models;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Recording
{
    private UUID mID;
    private String mTitle;
    private Date mDate;
    private String mDuration;

    public Recording(int recording_num)
    {
        this(UUID.randomUUID());
        mTitle = String.format(Locale.CANADA, "Recording %d", recording_num);
    }

    public Recording(UUID ID)
    {
        mID = ID;
        mDate = new Date();
    }

    public UUID getID()
    {
        return mID;
    }

    public void setID(UUID ID)
    {
        mID = ID;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public Date getDate()
    {
        return mDate;
    }

    public void setDate(Date date)
    {
        mDate = date;
    }

    public String getRecordingFileName()
    {
        return "VID_" + getID().toString() + ".mp4";
    }

    public String getThumbnailFileName() { return "IMG_" + getTitle() + ".png"; }

    public String getDuration()
    {
        return mDuration;
    }

    public void setDuration(String duration)
    {
        mDuration = duration;
    }

}
