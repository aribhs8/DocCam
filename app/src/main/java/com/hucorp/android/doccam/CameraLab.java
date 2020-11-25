package com.hucorp.android.doccam;

import android.content.Context;

import java.io.File;

public class CameraLab
{
    private static CameraLab sCameraLab;
    private Context mContext;

    public static CameraLab get(Context context) {
        if (sCameraLab == null) {
            sCameraLab = new CameraLab(context);
        }
        return sCameraLab;
    }

    private CameraLab(Context context) {
        mContext = context.getApplicationContext();
    }
}
