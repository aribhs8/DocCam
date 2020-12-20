package com.hucorp.android.doccam.fragments;

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

import com.hucorp.android.doccam.helper.CameraLab;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.models.Recording;
import com.hucorp.android.doccam.models.RecordingViewModel;
import com.hucorp.android.doccam.databinding.FragmentRecordingListBinding;
import com.hucorp.android.doccam.databinding.ListItemRecordingBinding;

import java.util.List;
import java.util.Objects;

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
        Objects.requireNonNull(getActivity()).setTitle("Recordings");
    }

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if(mRecordings.size()!=0){
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

        private RecordingHolder(ListItemRecordingBinding binding)
        {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new RecordingViewModel(getContext()));
        }

        public void bind(Recording recording)
        {
            mBinding.getViewModel().setRecording(recording);
            mBinding.executePendingBindings();
        }
    }

    public class RecordingAdapter extends RecyclerView.Adapter<RecordingHolder>
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
            Recording recording = mRecordings.get(position);
            holder.bind(recording);
        }

        @Override
        public int getItemCount()
        {
            return mRecordings.size();
        }
    }


}
