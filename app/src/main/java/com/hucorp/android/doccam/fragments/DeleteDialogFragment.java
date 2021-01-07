package com.hucorp.android.doccam.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hucorp.android.doccam.R;
import com.hucorp.android.doccam.interfaces.DeleteDialogListener;

public class DeleteDialogFragment extends DialogFragment
{
    private static DeleteDialogListener mListener;
    private static final String ARG_PLURAL = "plural";

    public static DeleteDialogFragment newInstance(boolean plural, DeleteDialogListener listener)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLURAL, plural);
        mListener = listener;

        DeleteDialogFragment fragment = new DeleteDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        /*
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray));

         */
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.DeleteDialogTheme);
        if ((boolean) getArguments().getSerializable(ARG_PLURAL))
        {
            builder.setMessage(R.string.dialog_delete_message_plural)
                    .setTitle(R.string.dialog_delete_title_plural);
        } else
        {
            builder.setMessage(R.string.dialog_delete_message)
                    .setTitle(R.string.dialog_delete_title);
        }


        builder.setPositiveButton(R.string.delete_caps, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mListener.onDialogPositiveClick(DeleteDialogFragment.this);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mListener.onDialogNegativeClick(DeleteDialogFragment.this);
            }
        });

        return builder.create();
    }
}
