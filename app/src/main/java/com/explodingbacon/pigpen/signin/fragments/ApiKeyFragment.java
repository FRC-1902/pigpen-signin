package com.explodingbacon.pigpen.signin.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.activities.MainActivity;

public class ApiKeyFragment extends DialogFragment {

    EditText key;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        key = getDialog().findViewById(R.id.key);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("API Key")
                .setCancelable(false)
                .setView(R.layout.fragment_api_key)
                .setPositiveButton("OK", (d, which) -> onDialogOk())
                .create();

        return dialog;
    }


    public void onDialogOk() {
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFS_REPO_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(MainActivity.PREFS_KEY_APIKEY, key.getText().toString());

        editor.apply();

        dismiss();
    }
}
