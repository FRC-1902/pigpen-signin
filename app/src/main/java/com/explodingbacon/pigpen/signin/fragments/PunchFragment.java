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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.activities.MainActivity;
import com.explodingbacon.pigpen.signin.api.models.HoursResponse;
import com.explodingbacon.pigpen.signin.api.models.PunchResponse;
import com.explodingbacon.pigpen.signin.api.models.SignedInResponse;
import com.explodingbacon.pigpen.signin.beans.Member;
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

public class PunchFragment extends DialogFragment {

    Member member;
    String secret;

    View actionContainer;
    View resultsContainer;
    View divider;
    View totalsContainer;

    Button doPunchButton;

    TextView memberName;
    TextView punchResults;
    TextView punchDuration;
    TextView totals;

    public static PunchFragment getInstance(Member member) {
        PunchFragment instance = new PunchFragment();
        instance.member = member;
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);

        SharedPreferences prefs = getContext().getSharedPreferences(MainActivity.PREFS_REPO_NAME, Context.MODE_PRIVATE);
        secret = prefs.getString(MainActivity.PREFS_KEY_APIKEY, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        doPunchButton = getDialog().findViewById(R.id.do_punch);

        actionContainer = getDialog().findViewById(R.id.actions_container);
        resultsContainer = getDialog().findViewById(R.id.results_container);
        divider = getDialog().findViewById(R.id.divider);
        totalsContainer = getDialog().findViewById(R.id.totals_container);

        memberName = getDialog().findViewById(R.id.member_name);
        punchResults = getDialog().findViewById(R.id.punch_result);
        punchDuration = getDialog().findViewById(R.id.duration);
        totals = getDialog().findViewById(R.id.totals);

        doPunchButton.setOnClickListener((v) -> doPunch());
        memberName.setText(member.getName());

        getStudentHours();
        getStudentPunchStatus();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setView(R.layout.fragment_punch)
                .setNegativeButton("Close", null)
                .create();

        return dialog;
    }

    private void doPunch() {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("secret", secret)
                .addFormDataPart("member", "" + member.getId())
                .build();

        Request request = new Request.Builder()
                .url(getString(R.string.api_base) + getString(R.string.api_do_punch))
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "There was an error. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("Punch", "Punch IOException", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    PunchResponse punchResponse = new Gson().fromJson(response.body().charStream(), PunchResponse.class);
                    updateUiWithPunchResponse(punchResponse);
                } else {
                    Log.e("Punch Response", "Non-200 response: " + response.toString());
                }
            }
        });
    }

    private void getStudentHours() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getString(R.string.api_base) + String.format(getString(R.string.api_member_hours_format), member.getId()))
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Hours", "Hours IOException", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    HoursResponse hoursResponse = new Gson().fromJson(response.body().charStream(), HoursResponse.class);
                    updateUiWithHoursResponse(hoursResponse);
                } else {
                    Log.e("Hours Reponse", "Non-200 response: " + response.toString());
                }
            }
        });
    }

    private void getStudentPunchStatus() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getString(R.string.api_base) + String.format(getString(R.string.api_member_signedin_format), member.getId()))
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Signed In Status", "Signed In Status IOException", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    SignedInResponse signedInResponse = new Gson().fromJson(response.body().charStream(), SignedInResponse.class);
                    updateUiWithSignedInResponse(signedInResponse);
                } else {
                    Log.e("Hours Reponse", "Non-200 response: " + response.toString());
                }
            }
        });
    }

    private void updateUiWithPunchResponse(PunchResponse response) {
        getActivity().runOnUiThread(() -> {
            punchResults.setText(String.format(getString(R.string.punch_summary_format),
                    response.getMember(), response.getPunch(), response.getTime()));

            if (response.getDuration() != null) {
                punchDuration.setText(String.format(getString(R.string.punch_duration_format),
                        response.getDuration()));
                punchDuration.setVisibility(View.VISIBLE);
            }

            actionContainer.setVisibility(View.GONE);
            resultsContainer.setVisibility(View.VISIBLE);
        });

        getStudentHours();
    }

    private void updateUiWithHoursResponse(HoursResponse hoursResponse) {
        getActivity().runOnUiThread(() -> {
            totals.setText(hoursResponse.toString());
            divider.setVisibility(View.VISIBLE);
            totalsContainer.setVisibility(View.VISIBLE);
        });
    }

    private void updateUiWithSignedInResponse(SignedInResponse signedInResponse) {
        getActivity().runOnUiThread(() -> {
            doPunchButton.setText(signedInResponse.getIsSignedIn() ? "Punch Out" : "Punch In");
            actionContainer.setVisibility(View.VISIBLE);
        });
    }
}
