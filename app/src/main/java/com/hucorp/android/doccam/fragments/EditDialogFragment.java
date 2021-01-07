package com.hucorp.android.doccam.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.interfaces.DeleteDialogListener;
import com.hucorp.android.doccam.models.Recording;

public class EditDialogFragment extends DialogFragment {

    private static EditDialogListener mListener;
    private static String currentRecordingName;
    private EditText newRecordingName;
    private TextView errorMessage;

    public static EditDialogFragment newInstance(String name, EditDialogListener listener)
    {
        Bundle args = new Bundle();
        mListener = listener;
        currentRecordingName = name;
        EditDialogFragment fragment = new EditDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_recording_dialog, null);

        builder.setView(view).setTitle("Edit recording name").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = newRecordingName.getText().toString();
                mListener.updateRecording(EditDialogFragment.this,newName);

            }
        });
        newRecordingName = view.findViewById(R.id.recording_name);
        newRecordingName.setText(currentRecordingName);
        errorMessage = view.findViewById(R.id.error_message);
        newRecordingName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(newRecordingName.getText().length() == 0){
                    errorMessage.setVisibility(View.VISIBLE);
                    ((androidx.appcompat.app.AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    errorMessage.setVisibility(View.GONE);
                    ((androidx.appcompat.app.AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        return builder.create();
    }

    public interface EditDialogListener{
        void updateRecording(EditDialogFragment dialog, String newTitle);
    }
}