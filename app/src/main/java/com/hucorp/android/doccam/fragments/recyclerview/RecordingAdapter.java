package com.hucorp.android.doccam.fragments.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.databinding.ListItemRecordingBinding;
import com.hucorp.android.doccam.interfaces.RecyclerViewCallback;
import com.hucorp.android.doccam.models.Recording;
import com.hucorp.android.doccam.models.RecordingViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>
{
    private List<Recording> mRecordings;
    private List<Recording> mMultiSelectList;
    private RecyclerViewCallback mCallback;

    public RecordingAdapter(List<Recording> recordings, RecyclerViewCallback callback)
    {
        mRecordings = recordings;
        mCallback = callback;
        mMultiSelectList = new ArrayList<>();
    }

    public void setMultiSelectList(List<Recording> multiSelectList)
    {
        mMultiSelectList = multiSelectList;
    }

    public void setFilter(List<Recording> newList){
        mRecordings = new ArrayList<>();
        mRecordings.addAll(newList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemRecordingBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.list_item_recording, parent, false);

        return new RecordingViewHolder(binding, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingViewHolder holder, int position)
    {
        Recording recording = mRecordings.get(position);
        holder.bind(recording);
    }

    @Override
    public int getItemCount()
    {
        return mRecordings.size();
    }

    public class RecordingViewHolder extends RecyclerView.ViewHolder
    {
        private ListItemRecordingBinding mBinding;
        private Context mContext;

        public RecordingViewHolder(ListItemRecordingBinding binding, Context context)
        {
            super(binding.getRoot());
            mBinding = binding;
            mContext = context;
            mBinding.setViewModel(new RecordingViewModel(context, mCallback));
        }

        public void bind(Recording recording)
        {
            mBinding.getViewModel().setRecording(recording);
            mBinding.executePendingBindings();

            if (mMultiSelectList.contains(recording))
            {
                ((CardView) itemView).setCardBackgroundColor(mContext.getResources().getColor(R.color.light_sky_blue));
            } else
            {
                ((CardView) itemView).setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
        }
    }
}
