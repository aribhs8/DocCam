package com.hucorp.android.doccam.helper;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.MenuRes;

import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.fragments.RecordingListFragment;
import com.hucorp.android.doccam.interfaces.OnActionItemClickListener;
import com.hucorp.android.doccam.models.Recording;

import java.util.List;

public class PrimaryActionModeCallback implements ActionMode.Callback
{
    private ActionMode mActionMode;
    private String mTitle;
    private boolean mForceRefresh = true;
    @MenuRes private int mMenuResId = 0;
    private List<Recording> mMultiSelectList;
    private OnActionItemClickListener mCallback;

    public PrimaryActionModeCallback(List<Recording> multiSelectList, OnActionItemClickListener callback)
    {
        mMultiSelectList = multiSelectList;
        mCallback = callback;
    }

    public void startActionMode(View v, @MenuRes int menuResId, String title)
    {
        mMenuResId = menuResId;
        mTitle = title;
        v.startActionMode(this);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        mActionMode = mode;
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(mMenuResId, menu);
        mode.setTitle(mTitle);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {
        mCallback.onActionItemClick(item);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
        mActionMode = null;
        mMultiSelectList.clear();
        if (mForceRefresh)
            mCallback.updateUI();
        mForceRefresh = true;
    }

    public void finishActionMode()
    {
        mActionMode.finish();
    }

    public void forceClose()
    {
        mForceRefresh = false;
        mActionMode.finish();
    }

    public ActionMode getActionMode()
    {
        return mActionMode;
    }
}
