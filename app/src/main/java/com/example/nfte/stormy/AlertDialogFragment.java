package com.example.nfte.stormy;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by NFTE on 9/7/2015.
 */
public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.error_title));
        builder.setMessage(context.getString(R.string.error_message));
        builder.setPositiveButton(context.getString(R.string.error_ok), null);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
