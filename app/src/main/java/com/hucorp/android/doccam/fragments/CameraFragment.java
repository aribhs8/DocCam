package com.hucorp.android.doccam.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.VideoCapture;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hucorp.android.doccam.helper.PictureUtils;
import com.hucorp.android.doccam.helper.Timer;
import com.hucorp.android.doccam.interfaces.CameraBarCallbacks;
import com.hucorp.android.doccam.activities.RecordingListActivity;
import com.hucorp.android.doccam.activities.SettingsActivity;
import com.hucorp.android.doccam.helper.CameraBar;
import com.hucorp.android.doccam.helper.Camera;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.helper.CameraLab;
import com.hucorp.android.doccam.models.Recording;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.*;

public class CameraFragment extends Fragment implements View.OnClickListener, CameraBarCallbacks, VideoCapture.OnVideoSavedCallback
{

    //Timer
    int countDownDisplay = 5;
    TextView timeButtonDisplay;
    ProgressBar timerBar;
    CountDownTimer countdown;
    ImageButton cancelTimer;
    public static boolean startedTimer;

    // Layout elements
    private CameraBar mToolbar;
    private ImageButton mCaptureBtn;
    private ImageButton mFileBtn;
    private ImageButton mStreamBtn;

    // Camera control
    private ExecutorService mCameraExecutor;
    private Camera mCamera;

    // Recording info
    private Recording mRecording;

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
        mRecording = CameraLab.get(getActivity()).getLastRecording();
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

        timeButtonDisplay = (TextView) v.findViewById(R.id.timebuttondisplay);
        timeButtonDisplay.setVisibility(View.GONE);

        timerBar = (ProgressBar) v.findViewById(R.id.timerBar);
        timerBar.setVisibility(View.GONE);

        cancelTimer = (ImageButton) v.findViewById(R.id.cancelTimer);

        mCaptureBtn = (ImageButton) v.findViewById(R.id.captureBtn);
        mFileBtn = (ImageButton) v.findViewById(R.id.fileBtn);
        mStreamBtn = (ImageButton) v.findViewById(R.id.streamBtn);

        mCamera.defineSurface((PreviewView) v.findViewById(R.id.viewFinder));
        mToolbar.setLayout((ConstraintLayout) v.findViewById(R.id.default_camera_toolbar));
        mToolbar.setCallback(this);
        initCamera();

        if (mRecording != null)
        {
            mFileBtn.setImageBitmap(PictureUtils.getScaledBitmap(CameraLab.get(getActivity())
                    .getThumbnailFile(mRecording).getPath(), getActivity()));
            mFileBtn.setClipToOutline(true);
        }

        mCaptureBtn.setOnClickListener(this);
        mFileBtn.setOnClickListener(this);
        mStreamBtn.setOnClickListener(this);
        cancelTimer.setOnClickListener(this);

        return v;
    }

    @Override
    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults)
    {
        mRecording.setDuration(Timer.get(getActivity()).getTimeElapsed());
        CameraLab.get(getActivity()).saveThumbnailFromVideo(mRecording);        // Create & save thumbnail
        CameraLab.get(getActivity()).addRecording(mRecording);
        mCamera.setNowRecording(false);
        Timer.get(getContext()).resetTimer();
        updateUI();
    }

    @Override
    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause)
    {
        Log.e(Constants.APP_TAG, message);
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
                if (CameraBar.fivetimer) {
                    startTimer(5000,1000);
                }

                else if (CameraBar.tentimer){
                    startTimer(10000,1000);
                }

                else{
                    mRecording = new Recording(CameraLab.get(getActivity()).getNumberOfRecordings()+1);
                    mCamera.record(getContext(),this, mRecording);
                    updateUI();
                }
            } else
            {
                mCamera.stopRecording();
            }
        } else if (b.getId() == R.id.fileBtn)
        {
            startActivity(new Intent(getActivity(), RecordingListActivity.class));
        } else if (b.getId() == R.id.streamBtn)
        {
            StreamingBottomSheetFragment fragment = StreamingBottomSheetFragment.newInstance();
            fragment.show(getFragmentManager(), "StreamingBottomSheetFragment");
        } else if (startedTimer)
        {
            if (b.getId() == R.id.cancelTimer){
                cancelTimer();
            }
        }
    }

    private void updateUI()
    {
        // Update elements
        Bitmap thumbnail = PictureUtils.getScaledBitmap(CameraLab.get(getActivity())
                .getThumbnailFile(mRecording).getPath(), getActivity());
        if (thumbnail != null)
        {
            mFileBtn.setImageBitmap(thumbnail);
            mFileBtn.setClipToOutline(true);
        }

        // Toggle buttons
        mFileBtn.setEnabled(!mCamera.isNowRecording());

        // Change layout elements by state
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

    /*==================================================
     *                  TIMER CODE
     * ==================================================*/

    void startTimer(int time, int interval) {
        mCaptureBtn.setEnabled(false);
        mToolbar.setLayout((ConstraintLayout) requireNonNull(getView()).findViewById(R.id.default_camera_toolbar));
        mStreamBtn.setVisibility(View.GONE);
        cancelTimer.setVisibility(View.VISIBLE);
        timeButtonDisplay.setVisibility(View.VISIBLE);
        countDownDisplay = time == 5000 ? 5 : 10;
        timerBar.setVisibility(View.VISIBLE);
        startedTimer = true;

        countdown = new CountDownTimer(time, interval) {

            public void onTick(long millisUntilFinished) {

                timeButtonDisplay.setText(String.format(Locale.getDefault(), "%d", countDownDisplay));
                countDownDisplay--;
            }

            public void onFinish() {
                mCaptureBtn.setEnabled(true);
                startedTimer = false;
                mStreamBtn.setVisibility(View.VISIBLE);
                cancelTimer.setVisibility(View.GONE);
                timeButtonDisplay.setVisibility(View.GONE);
                timerBar.setVisibility(View.GONE);
                mRecording = new Recording(CameraLab.get(getActivity()).getNumberOfRecordings() + 1);
                mCamera.record(getContext(), CameraFragment.this, mRecording);
                updateUI();
            }
        };
        countdown.start();
        CameraBar.fivetimer = false;
        CameraBar.tentimer = false;
    }

    //cancel timer
    void cancelTimer() {
        if(countdown!=null)
            mCaptureBtn.setEnabled(true);
            startedTimer = false;
            mStreamBtn.setVisibility(View.VISIBLE);
            cancelTimer.setVisibility(View.GONE);
            timeButtonDisplay.setVisibility(View.GONE);
            timerBar.setVisibility(View.GONE);
            countdown.cancel();
            mCaptureBtn.performClick();
    }

}

