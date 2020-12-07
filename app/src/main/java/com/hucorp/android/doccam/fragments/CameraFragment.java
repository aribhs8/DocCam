package com.hucorp.android.doccam.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.activities.RecordingListActivity;
import com.hucorp.android.doccam.activities.SettingsActivity;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

// Todo: Camera bug - After allowing permission the camera does not open. You need to restart for the camera to appear.
// Todo: Camera needs to link with storage system (yet to be made).

public class CameraFragment extends Fragment
{
    // constants
    private static final String TAG = "CameraXBasic";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(Manifest.permission.CAMERA);

    private Context mContext;
    private PreviewView mViewFinder;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private FloatingActionButton mRecordBtn;
    private ImageButton mStreamBtn;
    private ImageButton mFileBtn;

    //Top bar
    private ImageButton mTimerBtn;
    private ImageButton mSettingsBtn;


    public static CameraFragment newInstance()
    {
        return new CameraFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        mViewFinder = (PreviewView) v.findViewById(R.id.viewFinder);
        mRecordBtn = v.findViewById(R.id.recordBtn);
        mStreamBtn = v.findViewById(R.id.streamBtn);
        mFileBtn = v.findViewById(R.id.fileView);
        mSettingsBtn = v.findViewById(R.id.settings);

        if (allPermissionsGranted())
        {
            startCamera();
        }
        else
        {
            requestPermissions(REQUIRED_PERMISSIONS.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
        }

        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Record test", Toast.LENGTH_SHORT).show();;
            }
        });

        mStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Button is disabled", Toast.LENGTH_SHORT).show();
            }
        });

        mFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecordingListActivity.class);
                startActivity(intent);
            }
        });

        //Top bar
        mTimerBtn = v.findViewById(R.id.timer);

        mTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Open file system", Toast.LENGTH_SHORT).show();
            }
        });

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "I am at least here");
        if (requestCode == REQUEST_CODE_PERMISSIONS)
        {
            Log.d(TAG, "I am now here");
            if (allPermissionsGranted())
            {
                Log.d(TAG, "permission granted should startCamera()");
                startCamera();;
            }
            else
            {
                Toast.makeText(mContext, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                // *finish* activity
            }
        }
    }

    private void startCamera()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(mContext);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(mContext));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider)
    {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(mViewFinder.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)getActivity(), cameraSelector, preview);
    }

    private boolean allPermissionsGranted()
    {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
