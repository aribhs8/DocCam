package com.hucorp.android.doccam;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

// Todo: Use accompanying ViewModel with recording?

public class Recording
{
    private UUID mID;
    private String mTitle;
    private Date mDate;

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

    /*public String getPhotoFileName()
    {
        return "IMG_"+getID().toString() + ".jpg";
    }

     */
    public String getRecordingFileName()
    {
        return "VID_" + getID().toString() + ".mp4";
    }
}
