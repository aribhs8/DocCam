package com.hucorp.android.doccam.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hucorp.android.doccam.interfaces.CameraBarCallbacks;
import com.hucorp.android.doccam.activities.RecordingListActivity;
import com.hucorp.android.doccam.activities.SettingsActivity;
import com.hucorp.android.doccam.helper.CameraBar;
import com.hucorp.android.doccam.helper.Camera;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.helper.CameraLab;
import com.hucorp.android.doccam.models.Recording;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.*;

public class CameraFragment extends Fragment implements View.OnClickListener, CameraBarCallbacks
{
    // Layout elements
    private CameraBar mToolbar;
    private ImageButton mCaptureBtn;
    private ImageButton mFileBtn;

    // Camera control
    private ExecutorService mCameraExecutor;
    private Camera mCamera;

    public static CameraFragment newInstance()
    {
        return new CameraFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCameraExecutor = Executors.newSingleThreadExecutor();
        mCamera = Camera.get();
        mCamera.setNowRecording(false);
        mToolbar = CameraBar.newInstance(getContext());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mCamera.setNowRecording(false);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mCameraExecutor.shutdown();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        mCaptureBtn = (ImageButton) v.findViewById(R.id.captureBtn);
        mFileBtn = (ImageButton) v.findViewById(R.id.fileBtn);

        mCamera.defineSurface((PreviewView) v.findViewById(R.id.viewFinder));
        mToolbar.setLayout((ConstraintLayout) v.findViewById(R.id.default_camera_toolbar));
        mToolbar.setCallback(this);
        initCamera();

        mCaptureBtn.setOnClickListener(this);
        mFileBtn.setOnClickListener(this);

        return v;
    }

    /*==================================================
     *             HANDLE BUTTON PRESSES
     * ==================================================*/
    @Override
    public void onClick(View v)
    {
        ImageButton b = (ImageButton) v;
        if (b.getId() == R.id.captureBtn)
        {
            mCamera.setNowRecording(!mCamera.isNowRecording());
            if (mCamera.isNowRecording())
            {
                mCamera.record(getContext(), new Recording(CameraLab.get(getActivity()).getNumberOfRecordings()+1));
            } else
            {
                mCamera.stopRecording();
            } updateUI();
        } else if (b.getId() == R.id.fileBtn)
        {
            startActivity(new Intent(getActivity(), RecordingListActivity.class));
        }
    }

    private void updateUI()
    {
        // Toggle buttons
        mFileBtn.setEnabled(!mCamera.isNowRecording());

        // Change layout elements
        if (mCamera.isNowRecording())
        {
            mCaptureBtn.setImageResource(R.drawable.ic_baseline_stop_66);
            mToolbar.setLayout((ConstraintLayout) requireNonNull(getView()).findViewById(R.id.recording_camera_toolbar));
            mToolbar.updateDuration();
        } else
        {
            mCaptureBtn.setImageResource(R.drawable.ic_baseline_fiber_manual_record_66);
            mToolbar.setLayout((ConstraintLayout) requireNonNull(getView()).findViewById(R.id.default_camera_toolbar));
        }
    }

    @Override
    public void onSettingsClick()
    {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
    }

    @Override
    public void onTimerClick()
    {

    }

    /*==================================================
    *               PERMISSION CODE
    * ==================================================*/
    private void initCamera()
    {
        if (allPermissionsGranted())
        {
            mCamera.startCamera(getContext());
        } else
        {
            requestPermissions(Constants.REQUIRED_PERMISSIONS.toArray(new String[0]), Constants.REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted()
    {
        return ContextCompat.checkSelfPermission(requireNonNull(getContext()), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS)
        {
            if (allPermissionsGranted())
            {
                mCamera.startCamera(getContext());
            } else
            {
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                // Todo: Open a dialog here that tells the user they cannot use app without permission (remove Toast)
            }
        }
    }
}