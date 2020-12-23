package com.hucorp.android.doccam.helper;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.models.Recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class Camera
{
    private static Camera sCamera;

    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private PreviewView mViewFinder;
    private VideoCapture mVideoCapture;

    private boolean mNowRecording;

    public static Camera get()
    {
        if (sCamera == null)
        {
            sCamera = new Camera();
        }
        return sCamera;
    }

    public void defineSurface(PreviewView viewFinder)
    {
        mViewFinder = viewFinder;
    }

    public void startCamera(Context context)
    {
        mCameraProviderFuture = ProcessCameraProvider.getInstance(context);

        mCameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = mCameraProviderFuture.get();
                bindPreview(cameraProvider, context);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(Constants.appTag, "Critical Error - Camera not working");
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider, Context context)
    {
        Preview preview = new Preview.Builder()
                .build();

        mVideoCapture = new VideoCapture.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(mViewFinder.getSurfaceProvider());

        androidx.camera.core.Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview, mVideoCapture);
    }

    public boolean isNowRecording()
    {
        return mNowRecording;
    }

    public void setNowRecording(boolean nowRecording)
    {
        mNowRecording = nowRecording;
    }


    @SuppressLint("RestrictedApi")
    public void record(Context context, Recording recording)
    {
        File recordingFile = CameraLab.get(context).getRecordingFile(recording);
        File thumbnailFile = CameraLab.get(context).getThumbnailFile(recording);
        VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(recordingFile).build();

        mVideoCapture.startRecording(outputFileOptions, ContextCompat.getMainExecutor(context), new VideoCapture.OnVideoSavedCallback()
        {
            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults)
            {
                //recording.setDuration(Timer.getTimeElapsed());
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(recordingFile.toString(), MediaStore.Images.Thumbnails.MINI_KIND);
                try
                {
                    OutputStream fout = new FileOutputStream(thumbnailFile);
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, fout);
                    fout.close();
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), thumbnailFile.getAbsolutePath(), thumbnailFile.getName(), thumbnailFile.getName());
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                CameraLab.get(context).addRecording(recording);

                setNowRecording(false);
                //Timer.resetTimer();
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause)
            {
                Log.e(Constants.appTag, message);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    public void record(Context context, VideoCapture.OnVideoSavedCallback callback, Recording recording)
    {
        File recordingFile = CameraLab.get(context).getRecordingFile(recording);
        VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(recordingFile).build();

        mVideoCapture.startRecording(outputFileOptions, ContextCompat.getMainExecutor(context), callback);
    }

    @SuppressLint("RestrictedApi")
    public void stopRecording()
    {
        mVideoCapture.stopRecording();
    }
}
