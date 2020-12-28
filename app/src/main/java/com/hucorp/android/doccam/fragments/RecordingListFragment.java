package com.hucorp.android.doccam.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.helper.Camera;
import com.hucorp.android.doccam.helper.CameraLab;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.helper.PrimaryActionModeCallback;
import com.hucorp.android.doccam.interfaces.OnActionItemClickListener;
import com.hucorp.android.doccam.interfaces.RecyclerViewCallback;
import com.hucorp.android.doccam.models.Recording;
import com.hucorp.android.doccam.models.RecordingViewModel;
import com.hucorp.android.doccam.databinding.FragmentRecordingListBinding;
import com.hucorp.android.doccam.databinding.ListItemRecordingBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/*
Todo: Add deleting animation when recording is removed
Todo: Change appbar depending on how many items selected (hide edit button if > 1)
Todo: Add dialog warning about how many items are to be deleted
Todo: Undo video delete (future)
Todo: Prevent duplicate titles when editing (future)
 */
public class RecordingListFragment extends Fragment
{
    private List<Recording> mRecordings;

    public static RecordingListFragment newInstance()
    {
        return new RecordingListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        CameraLab cameraLab = CameraLab.get(getActivity());
        mRecordings = cameraLab.getRecordings();
        requireNonNull(getActivity()).setTitle("Recordings");
        setHasOptionsMenu(true);
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
        if (mRecordings.size() != 0)
        {
        FragmentRecordingListBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_recording_list, container, false);

        binding.recordingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recordingsRecyclerView.setAdapter(new RecordingAdapter(mRecordings));

        return binding.getRoot();}

        else{
            View v = inflater.inflate(R.layout.fragment_no_recordings, container, false);
            return v;
        }
    }

    private class RecordingHolder extends RecyclerView.ViewHolder
    {
        private ListItemRecordingBinding mBinding;

        private RecordingHolder(ListItemRecordingBinding binding, RecyclerViewCallback callback)
        {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new RecordingViewModel(getContext(), callback));
        }

        public void bind(Recording recording)
        {
            mBinding.getViewModel().setRecording(recording);
            mBinding.executePendingBindings();
        }
    }

    public class RecordingAdapter extends RecyclerView.Adapter<RecordingHolder> implements RecyclerViewCallback, OnActionItemClickListener
    {
        private List<Recording> mRecordings;
        private List<Recording> mMultiSelectList;
        private PrimaryActionModeCallback mPrimaryActionModeCallback;
        private Context mContext;

        public RecordingAdapter(List<Recording> recordings)
        {
            mRecordings = recordings;
            mMultiSelectList = new ArrayList<>();
            mPrimaryActionModeCallback = new PrimaryActionModeCallback(mMultiSelectList, this);
            mContext = getContext();
        }

        @NonNull
        @Override
        public RecordingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ListItemRecordingBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.list_item_recording, parent, false);

            return new RecordingHolder(binding, this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordingHolder holder, int position)
        {
            Recording recording = mRecordings.get(position);
            CardView cardView = (CardView) holder.itemView;
            if (mMultiSelectList.contains(recording))
            {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.light_sky_blue));
            } else
            {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
            holder.bind(recording);
        }

        @Override
        public int getItemCount()
        {
            return mRecordings.size();
        }

        @Override
        public boolean isMultiSelect()
        {
            return mMultiSelectList.size() > 0;
        }

        @Override
        public void multi_select(Recording recording)
        {
            if (mPrimaryActionModeCallback.getActionMode() == null)
            {
                mPrimaryActionModeCallback.startActionMode(requireNonNull(getView()), R.menu.fragment_recording_list_context_menu, "1");
            }

            if (mMultiSelectList.contains(recording))
            {
                mMultiSelectList.remove(recording);
            } else
            {
                mMultiSelectList.add(recording);
            }

            if (mMultiSelectList.size() > 0)
            {
                mPrimaryActionModeCallback.getActionMode().setTitle("" + mMultiSelectList.size());
            } else
            {
                mPrimaryActionModeCallback.getActionMode().setTitle("");
                mPrimaryActionModeCallback.finishActionMode();
            }

            refresh();
        }

        @Override
        public void onActionItemClick(MenuItem item)
        {
            CameraLab lab = CameraLab.get(getActivity());

            if (item.getItemId() == R.id.action_delete)
            {
                for (Recording recording : mMultiSelectList)
                {
                    Uri thumbnailUri = FileProvider.getUriForFile(mContext, "com.hucorp.android.doccam.fileprovider",
                            lab.getThumbnailFile(recording));
                    Uri recordingUri = FileProvider.getUriForFile(mContext, "com.hucorp.android.doccam.fileprovider",
                            lab.getRecordingFile(recording));

                    mContext.getContentResolver().delete(recordingUri, null, null);
                    mContext.getContentResolver().delete(thumbnailUri, null, null);
                    lab.deleteRecording(recording);
                }
                setRecordings(lab.getRecordings());
                Snackbar.make(getView(), mMultiSelectList.size() + " recording(s) deleted", Snackbar.LENGTH_SHORT).show();
            }

            mMultiSelectList.clear();
            refresh();
        }

        private void setRecordings(List<Recording> recordings)
        {
            mRecordings = recordings;
        }

        @Override
        public void refresh()
        {
            notifyDataSetChanged();
        }
    }
}
