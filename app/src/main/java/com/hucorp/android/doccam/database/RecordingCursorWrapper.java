package com.hucorp.android.doccam.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.hucorp.android.doccam.models.Recording;

import java.util.Date;
import java.util.UUID;

import static com.hucorp.android.doccam.database.RecordingDbSchema.*;

public class RecordingCursorWrapper extends CursorWrapper
{
    public RecordingCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Recording getRecording()
    {
        String uuidString = getString(getColumnIndex(RecordingTable.Cols.UUID));
        String title = getString(getColumnIndex(RecordingTable.Cols.TITLE));
        long date = getLong(getColumnIndex(RecordingTable.Cols.DATE));
        String duration = getString(getColumnIndex(RecordingTable.Cols.DURATION));

        Recording recording = new Recording(UUID.fromString(uuidString));
        recording.setTitle(title);
        recording.setDate(new Date(date));
        recording.setDuration(duration);

        return recording;
    }
}
