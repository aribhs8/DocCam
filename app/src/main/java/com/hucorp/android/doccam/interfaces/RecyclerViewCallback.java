package com.hucorp.android.doccam.interfaces;

import android.view.View;

import com.hucorp.android.doccam.models.Recording;

import java.util.ArrayList;
import java.util.List;

public interface RecyclerViewCallback
{
    public boolean isMultiSelect();
    public void multi_select(Recording recording);
}
