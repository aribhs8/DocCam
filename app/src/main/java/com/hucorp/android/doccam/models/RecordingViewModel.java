package com.hucorp.android.doccam.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.helper.Camera;
import com.hucorp.android.doccam.helper.CameraLab;
import com.hucorp.android.doccam.helper.PictureUtils;
import com.hucorp.android.doccam.helper.PrimaryActionModeCallback;
import com.hucorp.android.doccam.interfaces.RecyclerViewCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static androidx.core.content.ContextCompat.startActivity;

public class RecordingViewModel extends BaseObservable
{
    private Recording mRecording;
    private Context mContext;
    private RecyclerViewCallback mCallback;

    public RecordingViewModel(Context context, RecyclerViewCallback callback)
    {
        mContext = context;
        mCallback = callback;
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

    public Bitmap getThumbnail()
    {
        return PictureUtils.getScaledBitmap(CameraLab.get(mContext).getThumbnailFile(mRecording).getPath(), (Activity) mContext);
    }

    @BindingAdapter("android:loadImage")
    public static void loadImage(ImageView imageView, Bitmap bitmapImage)
    {
        imageView.setImageBitmap(bitmapImage);
        imageView.setClipToOutline(true);
    }

    @Bindable
    public String getDuration()
    {
        return "Duration: " + mRecording.getDuration();
    }

    public void onClick(View v)
    {
        if (mCallback.isMultiSelect())
        {
            mCallback.multi_select(mRecording);
        } else
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(mContext, "com.hucorp.android.doccam.fileprovider",
                    CameraLab.get(mContext).getRecordingFile(mRecording)), "video/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(mContext, intent, null);
        }
    }

    public boolean onLongClick(View v)
    {
        if (!mCallback.isMultiSelect())
        {
            mCallback.multi_select(mRecording);
        }

        return true;
    }
}
