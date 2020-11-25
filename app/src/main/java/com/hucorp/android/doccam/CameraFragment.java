package com.hucorp.android.doccam;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

public class CameraFragment extends Fragment
{
    // constants
    private static final String TAG = "CameraXBasic";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(Manifest.permission.CAMERA);


    public static CameraFragment newInstance()
    {
        return new CameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        if (allPermissionsGranted())
        {
            startCamera();
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
        }

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS)
        {
            if (allPermissionsGranted())
            {
                startCamera();;
            }
            else
            {
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                // *finish* activity
            }
        }
    }

    private void startCamera()
    {

    }

    private boolean allPermissionsGranted()
    {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
