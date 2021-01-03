package com.hucorp.android.doccam.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.interfaces.DeleteDialogListener;
import com.hucorp.android.doccam.models.Recording;

public class EditDialogFragment extends DialogFragment {

    private static EditDialogListener mListener;
    private EditText recordingName;

    public static EditDialogFragment newInstance(EditDialogListener listener)
    {
        Bundle args = new Bundle();
        mListener = listener;

        EditDialogFragment fragment = new EditDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray));

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_recording_dialog, null);

        builder.setView(view).setTitle("Edit Recording").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = recordingName.getText().toString();
                mListener.updateRecording(newName);

            }
        });
        recordingName = view.findViewById(R.id.recording_name);

        return builder.create();
    }


    public interface EditDialogListener{
        void updateRecording(String newTitle);
    }
}