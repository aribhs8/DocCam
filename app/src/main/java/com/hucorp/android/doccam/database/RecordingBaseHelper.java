package com.hucorp.android.doccam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.hucorp.android.doccam.database.RecordingDbSchema.*;

public class RecordingBaseHelper extends SQLiteOpenHelper
{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "recordingBase.db";

    public RecordingBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + RecordingTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                RecordingTable.Cols.UUID + ", " +
                RecordingTable.Cols.TITLE + ", " +
                RecordingTable.Cols.DATE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
