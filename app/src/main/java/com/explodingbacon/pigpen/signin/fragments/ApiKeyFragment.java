package com.explodingbacon.pigpen.signin.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.activities.MainActivity;
import com.explodingbacon.pigpen.signin.api.models.KeyExchangeResponse;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyFragment extends DialogFragment {

    Activity context;
    EditText key;

    public ApiKeyFragment(Activity context) {
        this.context = context;
    }

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
        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("API Key")
                .setCancelable(false)
                .setView(R.layout.fragment_api_key)
                .setPositiveButton("OK", (d, which) -> onDialogOk())
                .create();

        return dialog;
    }


    public void onDialogOk() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(context.getString(R.string.api_base) +
                        context.getString(R.string.api_token_exchange))
                .post(new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("key", key.getText().toString())
                        .build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(context, "IOException", Toast.LENGTH_SHORT).show();
                Log.e("Token Exchange", "HTTP Error", e);

                dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //TODO: Fix these toasts and remove the exceptions
                Looper.prepare();
                if (response.code() == 200) {
                    KeyExchangeResponse apiResponse = new Gson().fromJson(response.body().string(), KeyExchangeResponse.class);

                    if (apiResponse.getSuccess()) {
                        onTokenReceived(apiResponse.getToken());
                    } else {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        throw new RuntimeException("Non-Successful Response: " + apiResponse.getMessage());
                    }
                } else {
                    Toast.makeText(context, "Non-200 Response", Toast.LENGTH_SHORT).show();
                    Log.e("Token Exchange", "Non-200 Repsonse: " + response.toString());
                    throw new RuntimeException("Non-Successful Response: " + response.toString());
                }
            }
        });
    }


    public void onTokenReceived(String token) {
        context.runOnUiThread(() -> {
            if (token != null) {
                SharedPreferences prefs = context.getSharedPreferences(MainActivity.PREFS_REPO_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString(MainActivity.PREFS_KEY_APIKEY, token);

                editor.apply();
            }

            dismiss();
        });
    }
}
