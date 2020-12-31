package com.hucorp.android.doccam.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.databinding.FragmentRecordingListBinding;
import com.hucorp.android.doccam.fragments.recyclerview.CustomItemAnimator;
import com.hucorp.android.doccam.fragments.recyclerview.RecordingAdapter;
import com.hucorp.android.doccam.helper.CameraLab;
import com.hucorp.android.doccam.helper.PrimaryActionModeCallback;
import com.hucorp.android.doccam.interfaces.DeleteDialogListener;
import com.hucorp.android.doccam.interfaces.OnActionItemClickListener;
import com.hucorp.android.doccam.interfaces.RecyclerViewCallback;
import com.hucorp.android.doccam.models.Recording;

import java.util.ArrayList;
import java.util.List;

public class RecordingListFragment extends Fragment
        implements RecyclerViewCallback, OnActionItemClickListener, DeleteDialogListener
{
    private List<Recording> mRecordings;
    private List<Recording> mMultiSelectList;

    private FragmentRecordingListBinding mBinding;
    private RecordingAdapter mAdapter;
    private PrimaryActionModeCallback mActionMode;

    private Context mContext;

    public static RecordingListFragment newInstance() { return new RecordingListFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mRecordings = CameraLab.get(getActivity()).getRecordings();
        mMultiSelectList = new ArrayList<>();
        mActionMode = new PrimaryActionModeCallback(mMultiSelectList, this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateLayout();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recording_list, menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_recording_list, container, false);
        mContext = getContext();

        mAdapter = new RecordingAdapter(mRecordings, this);

        mBinding.recordingsRecyclerView.setAdapter(mAdapter);
        mBinding.recordingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recordingsRecyclerView.setHasFixedSize(true);
        mBinding.recordingsRecyclerView.setItemAnimator(new CustomItemAnimator());

        return mBinding.getRoot();
    }

    public void updateLayout()
    {
        if (mRecordings.size() == 0)
        {
            mBinding.noRecordings.setVisibility(View.VISIBLE);
        } else
        {
            mBinding.noRecordings.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isMultiSelect()
    {
        return mMultiSelectList.size() > 0;
    }

    @Override
    public void multi_select(Recording recording)
    {
        if (mActionMode.getActionMode() == null)
            mActionMode.startActionMode(mBinding.getRoot(), R.menu.fragment_recording_list_context_menu, "1");

        if (mMultiSelectList.contains(recording))
        {
            mMultiSelectList.remove(recording);
        } else
        {
            mMultiSelectList.add(recording);
        }

        if (mMultiSelectList.size() > 0)
        {
            mActionMode.getActionMode().setTitle("" + mMultiSelectList.size());
        } else
        {
            mActionMode.getActionMode().setTitle("");
            mActionMode.finishActionMode();
        }

        updateUI();
    }

    @Override
    public void onActionItemClick(MenuItem item)
    {
        if (item.getItemId() == R.id.action_delete)
        {
            DeleteDialogFragment dialog = DeleteDialogFragment.newInstance(mMultiSelectList.size() > 1, this);
            assert getFragmentManager() != null;
            dialog.show(getFragmentManager(), "DeleteDialogFragment");
        }
    }

    @Override
    public void updateUI()
    {
        mAdapter.setMultiSelectList(mMultiSelectList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment)
    {
        for (Recording recording : mMultiSelectList)
        {
            int position = mRecordings.indexOf(recording);
            mRecordings.remove(position);
            mAdapter.notifyItemRemoved(position);
            CameraLab.get(getContext()).wipeRecordingData(mContext, recording);
        }

        dialogFragment.dismiss();
        updateLayout();
        Snackbar.make(mBinding.getRoot(), mMultiSelectList.size() + " recording(s) were deleted", Snackbar.LENGTH_SHORT).show();
        mActionMode.forceClose();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment)
    {
        dialogFragment.dismiss();
        mActionMode.finishActionMode();
    }
}