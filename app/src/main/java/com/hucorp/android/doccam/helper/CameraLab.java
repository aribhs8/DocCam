package com.hucorp.android.doccam.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.hucorp.android.doccam.database.RecordingBaseHelper;
import com.hucorp.android.doccam.database.RecordingCursorWrapper;
import com.hucorp.android.doccam.models.Recording;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

        try (RecordingCursorWrapper cursor = queryRecordings(null, null))
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                recordings.add(cursor.getRecording());
                cursor.moveToNext();
            }
        }

        return recordings;
    }

    public Recording getRecording(UUID id)
    {

        try (RecordingCursorWrapper cursor = queryRecordings(
                RecordingTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        ))
        {
            if (cursor.getCount() == 0)
            {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getRecording();
        }
    }

    public int getNumberOfRecordings()
    {
        return getRecordings().size();
    }

    public Recording getLastRecording()
    {
        return getRecordings().get(getRecordings().size() - 1);
    }

    public void updateRecording(Recording recording)
    {
        String uuidString = recording.getID().toString();
        ContentValues values = getContentValues(recording);

        mDatabase.update(RecordingTable.NAME, values,
                RecordingTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public void deleteRecording(Recording recording)
    {
        String uuidString = recording.getID().toString();

        mDatabase.delete(RecordingTable.NAME,
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
        values.put(RecordingTable.Cols.DURATION, recording.getDuration());

        return values;
    }

    public File getRecordingFile(Recording recording)
    {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, recording.getRecordingFileName());
    }

    public File getThumbnailFile(Recording recording)
    {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, recording.getThumbnailFileName());
    }

    public void saveThumbnailFromVideo(Recording recording)
    {
        File thumbnailFile = getThumbnailFile(recording);
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(getRecordingFile(recording).toString(),
                MediaStore.Images.Thumbnails.MINI_KIND);
        try
        {
            OutputStream fout = new FileOutputStream(thumbnailFile);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, fout);
            fout.close();
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), thumbnailFile.getAbsolutePath(), thumbnailFile.getName(), thumbnailFile.getName());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
