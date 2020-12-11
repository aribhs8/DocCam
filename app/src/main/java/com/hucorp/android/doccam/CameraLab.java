package com.hucorp.android.doccam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hucorp.android.doccam.database.RecordingBaseHelper;
import com.hucorp.android.doccam.database.RecordingCursorWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hucorp.android.doccam.database.RecordingDbSchema.*;

public class CameraLab
{
    private static CameraLab sCameraLab;

    //private List<Recording> mRecordings;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CameraLab get(Context context) {
        if (sCameraLab == null) {
            sCameraLab = new CameraLab(context);
        }
        return sCameraLab;
    }

    private CameraLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new RecordingBaseHelper(mContext).getWritableDatabase();
    }

    public List<Recording> getRecordings()
    {
        //return mRecordings;
        List<Recording> recordings = new ArrayList<>();

        RecordingCursorWrapper cursor = queryRecordings(null, null);

        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                recordings.add(cursor.getRecording());
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        return recordings;
    }

    public Recording getRecording(UUID id)
    {
        RecordingCursorWrapper cursor = queryRecordings(
                RecordingTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try
        {
            if (cursor.getCount() == 0)
            {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getRecording();
        }
        finally
        {
            cursor.close();
        }
    }

    public int getNumberOfRecordings()
    {
        return getRecordings().size();
    }

    public void updateRecording(Recording recording)
    {
        String uuidString = recording.getID().toString();
        ContentValues values = getContentValues(recording);

        mDatabase.update(RecordingTable.NAME, values,
                RecordingTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private RecordingCursorWrapper queryRecordings(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                RecordingTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new RecordingCursorWrapper(cursor);
    }

    public void addRecording(Recording r)
    {
        ContentValues values = getContentValues(r);
        mDatabase.insert(RecordingTable.NAME, null, values);
    }

    public static ContentValues getContentValues(Recording recording)
    {
        ContentValues values = new ContentValues();
        values.put(RecordingTable.Cols.UUID, recording.getID().toString());
        values.put(RecordingTable.Cols.TITLE, recording.getTitle());
        values.put(RecordingTable.Cols.DATE, recording.getDate().getTime());

        return values;
    }

    public File getPhotoFile(Recording recording)
    {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, recording.getPhotoFileName());
    }
}
