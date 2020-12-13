package com.hucorp.android.doccam.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.hucorp.android.doccam.CameraLab;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.Recording;
import com.hucorp.android.doccam.activities.RecordingListActivity;
import com.hucorp.android.doccam.activities.SettingsActivity;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private Button mFiveTimerBtn;
    private Button mTenTimerBtn;
    private ProgressBar timerBarBtn;

    private TextView mtimedisplay;



    // Camera control
    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private VideoCapture mVideoCapture;
    private ExecutorService mCameraExecutor;
    private File mRecordingFile;
    private boolean mIsRecording;
    public boolean mFlash;
    public boolean mTimer;

    public boolean m5Timer;
    public boolean m10Timer;

    public static CameraFragment newInstance() { return new CameraFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mIsRecording = false;
        mCameraExecutor = Executors.newSingleThreadExecutor();
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

        // Initialize layout elements
        mViewFinder = (PreviewView) v.findViewById(R.id.viewFinder);
        mCaptureBtn = (ImageButton) v.findViewById(R.id.captureBtn);
        mLeftBtn = (ImageButton) v.findViewById(R.id.left_cameraBtn);
        mSettingsBtn = (ImageButton) v.findViewById(R.id.settingsBtn);
        mFlashBtn = (ImageButton) v.findViewById(R.id.flashBtn);

        timerBarBtn = (ProgressBar) v.findViewById(R.id.timerBar);
        timerBarBtn.setVisibility(v.GONE);

        // Timer
        mTimerBtn = (ImageButton) v.findViewById(R.id.timerBtn);
        mFiveTimerBtn = (Button) v.findViewById(R.id.fivetimer);
        mTenTimerBtn = (Button) v.findViewById(R.id.tentimer);

        mTimer = true;
        mFlash = true;
        m5Timer = false;
        m10Timer = false;

        mFiveTimerBtn.setVisibility(v.GONE);
        mTenTimerBtn.setVisibility(v.GONE);
        mtimedisplay = (TextView) v.findViewById(R.id.timedisplay);

        mtimedisplay.setVisibility(v.GONE);

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
            @SuppressLint("RestrictedApi")
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
                    //takePhoto(recording);
                    record(recording);

                    mCaptureBtn.setImageResource(R.drawable.ic_baseline_fiber_manual_record_66);
                } else
                {
                    if (m5Timer) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            timerBarBtn.setVisibility(v.VISIBLE);
                            timerBarBtn.setProgress(10,true);
                        }
                        Runnable r = new Runnable() {
                            @Override
                            public void run(){
                                timerBarBtn.setVisibility(v.GONE);
                                mIsRecording = true;
                                mLeftBtn.setEnabled(false);
                                mCaptureBtn.setImageResource(R.drawable.ic_baseline_stop_66);
                                mVideoCapture.stopRecording();
                            }
                        };
                        Handler h = new Handler();
                        h.postDelayed(r, 5000); // <-- the "1000" is the delay time in miliseconds.
                    }

                    else if (m10Timer) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            timerBarBtn.setVisibility(v.VISIBLE);
                            timerBarBtn.setProgress(10,true);
                        }

                        Runnable r = new Runnable() {
                            @Override
                            public void run(){
                                timerBarBtn.setVisibility(v.GONE);
                                mIsRecording = true;
                                mLeftBtn.setEnabled(false);
                                mCaptureBtn.setImageResource(R.drawable.ic_baseline_stop_66);
                                mVideoCapture.stopRecording();
                            }
                        };
                        Handler h = new Handler();
                        h.postDelayed(r, 10000); // <-- the "1000" is the delay time in miliseconds.
                    }

                    else{
                        mIsRecording = true;
                        mLeftBtn.setEnabled(false);
                        mCaptureBtn.setImageResource(R.drawable.ic_baseline_stop_66);
                        mVideoCapture.stopRecording();
                    }
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
                } else
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
                    mTenTimerBtn.setVisibility(v.VISIBLE);
                    mTenTimerBtn.startAnimation(animation1);
                    mFiveTimerBtn.setVisibility(v.VISIBLE);
                    mFiveTimerBtn.startAnimation(animation1);

                } else
                {
                    mTimer = true;
                    Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);

                    mTenTimerBtn.startAnimation(animation2);
                    mFiveTimerBtn.startAnimation(animation2);
                    mFiveTimerBtn.setVisibility(v.GONE);
                    mTenTimerBtn.setVisibility(v.GONE);

                }
            }
        });

        mFiveTimerBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v){

                mFiveTimerBtn.clearAnimation();
                mTenTimerBtn.clearAnimation();

                Animation animation3 = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);

                mTenTimerBtn.startAnimation(animation3);
                mFiveTimerBtn.startAnimation(animation3);

                mFiveTimerBtn.setVisibility(v.GONE);
                mTenTimerBtn.setVisibility(v.GONE);

                mTimer = true;

                m10Timer = false;

                if (m5Timer)
                {
                    m5Timer = false;
                    mtimedisplay.setText("5s");
                    mtimedisplay.setVisibility(v.GONE);

                } else
                {
                    m5Timer = true;
                    mtimedisplay.setText("5s");
                    mtimedisplay.setVisibility(v.VISIBLE);

                }
            }
        });

        mTenTimerBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v){
                mFiveTimerBtn.clearAnimation();
                mTenTimerBtn.clearAnimation();

                Animation animation3 = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);

                mTenTimerBtn.startAnimation(animation3);
                mFiveTimerBtn.startAnimation(animation3);

                mFiveTimerBtn.setVisibility(v.GONE);
                mTenTimerBtn.setVisibility(v.GONE);

                mTimer = true;

                m5Timer = false;

                if (m10Timer)
                {
                    m10Timer = false;
                    mtimedisplay.setText("10s");
                    mtimedisplay.setVisibility(v.GONE);


                } else
                {
                    m10Timer = true;
                    mtimedisplay.setText("10s");
                    mtimedisplay.setVisibility(v.VISIBLE);
                }
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
                startCamera();
            } else
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

    @SuppressLint("RestrictedApi")
    private void record(Recording recording)
    {
        mRecordingFile = CameraLab.get(getActivity()).getRecordingFile(recording);
        VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(mRecordingFile).build();

        mVideoCapture.startRecording(outputFileOptions, ContextCompat.getMainExecutor(getContext()), new VideoCapture.OnVideoSavedCallback()
        {
            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults)
            {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.hucorp.android.doccam.fileprovider",
                        mRecordingFile);
                String message = "Photo capture succeeded: " + uri.toString();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause)
            {

            }
        });
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

        mVideoCapture = new VideoCapture.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(mViewFinder.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) Objects.requireNonNull(getActivity()), cameraSelector, preview, mVideoCapture);
    }

    private boolean allPermissionsGranted()
    {
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
