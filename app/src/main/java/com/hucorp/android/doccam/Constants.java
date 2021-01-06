package com.hucorp.android.doccam;

import android.Manifest;

import com.google.api.services.youtube.YouTubeScopes;

import java.util.Arrays;
import java.util.List;

public class Constants
{
    // Camera
    public static final String APP_TAG = "DocCam";
    public static final int REQUEST_CODE_PERMISSIONS = 10;
    public static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);

    // Dialogs
    public static final String DIALOG_DELETE = "DialogDelete";

    // Material Sheets
    public static final String SHEET_STREAM = "SheetStream";

    // YouTube
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int RC_SIGN_IN = 0;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final String BUTTON_TEXT = "Call YouTube Data API";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY };
}
