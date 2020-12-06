package com.hucorp.android.doccam.fragments;

import android.graphics.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hucorp.android.doccam.CameraLab;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.Recording;
import com.hucorp.android.doccam.databinding.FragmentRecordingListBinding;
import com.hucorp.android.doccam.databinding.ListItemRecordingBinding;

import java.util.List;

public class RecordingListFragment extends Fragment
{
    private RecyclerView mRecordingList;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        FragmentRecordingListBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_recording_list, container, false);

        binding.recordingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recordingsRecyclerView.setAdapter(new RecordingAdapter(mRecordings));

        return binding.getRoot();
    }

    private class RecordingHolder extends RecyclerView.ViewHolder
    {
        private ListItemRecordingBinding mBinding;

        private RecordingHolder(ListItemRecordingBinding binding)
        {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    private class RecordingAdapter extends RecyclerView.Adapter<RecordingHolder>
    {
        private List<Recording> mRecordings;

        public RecordingAdapter(List<Recording> recordings)
        {
            mRecordings = recordings;
        }

        @NonNull
        @Override
        public RecordingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ListItemRecordingBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.list_item_recording, parent, false);
            return new RecordingHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordingHolder holder, int position)
        {

        }

        @Override
        public int getItemCount()
        {
            return mRecordings.size();
        }
    }
}