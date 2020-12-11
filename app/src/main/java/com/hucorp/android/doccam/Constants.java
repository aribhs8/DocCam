package com.hucorp.android.doccam;

import android.Manifest;

import java.util.Arrays;
import java.util.List;

public class Constants
{
    // Camera
    public static final String appTag = "DocCam";
    public static final int REQUEST_CODE_PERMISSIONS = 10;
    public static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
}
