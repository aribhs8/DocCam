package com.hucorp.android.doccam;

import java.util.Date;
import java.util.UUID;

public class Recording
{
    private UUID mID;
    private String mTitle;
    private Date mDate;

    public Recording()
    {
        this(UUID.randomUUID());
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
}
