package com.explodingbacon.pigpen.signin.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.activities.MainActivity;
import com.explodingbacon.pigpen.signin.api.models.AddMemberResponse;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddMemberFragment extends DialogFragment {

    MemberAddedListener listener;

    EditText first;
    EditText last;

    Spinner role;

    String secret;

    public static AddMemberFragment getInstance(MemberAddedListener listener) {
        AddMemberFragment instance = new AddMemberFragment();
        instance.listener = listener;
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFS_REPO_NAME, Context.MODE_PRIVATE);
        secret = prefs.getString(MainActivity.PREFS_KEY_APIKEY, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        first = getDialog().findViewById(R.id.first);
        last = getDialog().findViewById(R.id.last);
        role = getDialog().findViewById(R.id.role);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Add Member")
                .setPositiveButton("Add", (d, w) -> doAddMember()) //TODO: This
                .setNegativeButton("Close", null)
                .setView(R.layout.fragment_add_member)
                .create();

        return dialog;
    }

    private void doAddMember() {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("first", first.getText().toString())
                .addFormDataPart("last", last.getText().toString())
                .addFormDataPart("role", getRoleSlug())
                .addFormDataPart("secret", secret)
                .build();

        Request request = new Request.Builder()
                .url(getString(R.string.api_base) + getString(R.string.api_member_add))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "There was an error. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("Add Member", "Add Member IOException", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    AddMemberResponse addResponse = new Gson().fromJson(response.body().string(), AddMemberResponse.class);
                    if (addResponse.getSuccess()) {
                        listener.onMemberAdded();
                        dismiss();
                    } else {
                        Looper.prepare();
                        Toast.makeText(getActivity(), "There was an error. Please try again.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } else {
                    Log.e("Add Member Response", "Non-200 response: " + response.toString());
                }
            }
        });
    }

    private String getRoleSlug() {
        return getResources().getStringArray(R.array.role_slugs)[role.getSelectedItemPosition()];
    }

    public interface MemberAddedListener {
        void onMemberAdded();
    }
}
