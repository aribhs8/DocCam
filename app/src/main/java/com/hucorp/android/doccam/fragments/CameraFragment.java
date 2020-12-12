package com.hucorp.android.doccam.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.hucorp.android.doccam.CameraLab;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.Recording;
import com.hucorp.android.doccam.activities.RecordingListActivity;
import com.hucorp.android.doccam.activities.SettingsActivity;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

// Todo: Check copied over camera code with that of documentation; Can I simplify any camera code?
// Todo: Incorporate video recording code before simplifying camera code
// Todo: I can't move permissions code to another file but can I move the camera methods to an interface or something?

public class CameraFragment extends Fragment
{
    // Layout elements
    private PreviewView mViewFinder;
    private ImageButton mCaptureBtn;
    private ImageButton mLeftBtn;
    private ImageButton mSettingsBtn;
    private ImageButton mFlashBtn;

    //Timer
    private ImageButton mTimerBtn;
    private Button mfiveTimerBtn;
    private Button mtenTimerBtn;

    // Camera control
    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private boolean mIsRecording;
    public boolean mFlash;
    public boolean mTimer;

    public static CameraFragment newInstance() { return new CameraFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mIsRecording = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        // Initialize layout elements
        mViewFinder = (PreviewView) v.findViewById(R.id.viewFinder);
        mCaptureBtn = (ImageButton) v.findViewById(R.id.captureBtn);
        mLeftBtn = (ImageButton) v.findViewById(R.id.left_cameraBtn);
        mSettingsBtn = (ImageButton) v.findViewById(R.id.settingsBtn);
        mFlashBtn = (ImageButton) v.findViewById(R.id.flashBtn);

        //Timer
        mTimerBtn = (ImageButton) v.findViewById(R.id.timerBtn);
        mfiveTimerBtn = (Button) v.findViewById(R.id.fivetimer);
        mtenTimerBtn = (Button) v.findViewById(R.id.tentimer);
        mTimer = true;
        mFlash = true;
        mfiveTimerBtn.setVisibility(v.GONE);
        mtenTimerBtn.setVisibility(v.GONE);

        initCamera();               // Check for permissions and start camera
        controlCameraBarInput();    // Poll for user input on camera bar
        controlAppBarInput();       // Poll for user input in app bar

        return v;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void controlCameraBarInput()
    {
        mCaptureBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        mCaptureBtn.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                        mCaptureBtn.setAlpha(1.0f);
                        break;
                }
                return false;
            }
        });

        mCaptureBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mIsRecording)
                {
                    mIsRecording = false;
                    mLeftBtn.setEnabled(true);

                    CameraLab lab = CameraLab.get(getActivity());
                    Recording recording = new Recording(lab.getNumberOfRecordings()+1);
                    lab.addRecording(recording);

                    mCaptureBtn.setImageResource(R.drawable.ic_baseline_fiber_manual_record_66);
                    Toast.makeText(getContext(), recording.getTitle() + " created", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mIsRecording = true;
                    mLeftBtn.setEnabled(false);
                    mCaptureBtn.setImageResource(R.drawable.ic_baseline_stop_66);
                    Toast.makeText(getContext(), "Capture button has been placed in record mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLeftBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), RecordingListActivity.class);
                startActivity(intent);

            }
        });
    }

    private void controlAppBarInput()
    {
        mSettingsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
            }
        });

        mFlashBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mFlash)
                {
                    mFlash = false;
                    mFlashBtn.setImageResource(R.drawable.flash);
                }
                else
                {
                    mFlash = true;
                    mFlashBtn.setImageResource(R.drawable.flashoff);
                }
            }
        });


        mTimerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){

                if (mTimer)
                {
                    mTimer = false;
                    Animation animation1 = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
                    mtenTimerBtn.setVisibility(v.VISIBLE);
                    mtenTimerBtn.startAnimation(animation1);
                    mfiveTimerBtn.setVisibility(v.VISIBLE);
                    mfiveTimerBtn.startAnimation(animation1);
                }
                else
                {
                    mTimer = true;
                    Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);

                    mtenTimerBtn.startAnimation(animation2);
                    mfiveTimerBtn.startAnimation(animation2);
                    mfiveTimerBtn.setVisibility(v.GONE);
                    mtenTimerBtn.setVisibility(v.GONE);
                }

                // AnimationUtils.loadAnimation(this,)

            }
        });




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS)
        {
            if (allPermissionsGranted())
            {
                startCamera();;
            }
            else
            {
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                // Todo: Open a dialog here that tells the user they cannot use app without permission (remove Toast)
            }
        }
    }

    private void initCamera()
    {
        if (allPermissionsGranted())
        {
            startCamera();
        } else
        {
            requestPermissions(Constants.REQUIRED_PERMISSIONS.toArray(new String[0]), Constants.REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera()
    {
        mCameraProviderFuture = ProcessCameraProvider.getInstance(Objects.requireNonNull(getContext()));

        mCameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = mCameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider)
    {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(mViewFinder.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) Objects.requireNonNull(getActivity()), cameraSelector, preview);
    }

    private boolean allPermissionsGranted()
    {
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
