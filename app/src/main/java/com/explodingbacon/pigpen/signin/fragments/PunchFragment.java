package com.explodingbacon.pigpen.signin.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.activities.MainActivity;
import com.explodingbacon.pigpen.signin.api.models.HoursResponse;
import com.explodingbacon.pigpen.signin.api.models.PunchResponse;
import com.explodingbacon.pigpen.signin.api.models.SignedInResponse;
import com.explodingbacon.pigpen.signin.api.models.TeambuildingResponse;
import com.explodingbacon.pigpen.signin.beans.Member;
import com.explodingbacon.pigpen.signin.utils.TeambuildingUtils;
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

    MainActivity activity;

    Member member;
    SignedInResponse signedInResponse;
    String secret;
    Boolean didPunch = false;

    View actionContainer;
    View resultsContainer;
    View divider;
    View totalsContainer;

    View tbInstructions;
    TextView tbQuestion;
    View tbOrText;
    Button punchButtonOne;
    Button punchButtonTwo;

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

        if ((activity = (MainActivity) getActivity()) == null) {
            dismiss();
        }

        SharedPreferences prefs = activity.getSharedPreferences(MainActivity.PREFS_REPO_NAME, Context.MODE_PRIVATE);
        secret = prefs.getString(MainActivity.PREFS_KEY_APIKEY, null);
        Log.i("API Key", secret);
    }

    @Override
    public void onStart() {
        super.onStart();

        tbInstructions = getDialog().findViewById(R.id.teambuilding_instructions);
        tbQuestion = getDialog().findViewById(R.id.teambuilding_question);
        tbOrText = getDialog().findViewById(R.id.tb_or);
        punchButtonOne = getDialog().findViewById(R.id.do_punch_one);
        punchButtonTwo = getDialog().findViewById(R.id.do_punch_two);

        actionContainer = getDialog().findViewById(R.id.actions_container);
        resultsContainer = getDialog().findViewById(R.id.results_container);
        divider = getDialog().findViewById(R.id.divider);
        totalsContainer = getDialog().findViewById(R.id.totals_container);

        memberName = getDialog().findViewById(R.id.member_name);
        punchResults = getDialog().findViewById(R.id.punch_result);
        punchDuration = getDialog().findViewById(R.id.duration);
        totals = getDialog().findViewById(R.id.totals);

        punchButtonOne.setOnClickListener(this::doPunch);
        punchButtonTwo.setOnClickListener(this::doPunch);
        memberName.setText(member.getName());

        getStudentHours();
        getStudentPunchStatus();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setView(R.layout.fragment_punch)
                .setNegativeButton("Close", null)
                .create();

        return dialog;
    }

    private void doPunch(View v) {
        if (didPunch) return;
        didPunch = true;

        OkHttpClient client = new OkHttpClient();

        //Do actual punch
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
                Toast.makeText(activity, "There was an error. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("Punch", "Punch IOException", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    PunchResponse punchResponse = new Gson().fromJson(response.body().string(), PunchResponse.class);
                    updateUiWithPunchResponse(punchResponse);
                } else {
                    Log.e("Punch Response", "Non-200 response: " + response.toString());
                }
            }
        });

        if (signedInResponse.getIsSignedIn()) return;

        //Do teambuilding
        TeambuildingResponse tConfig = TeambuildingUtils.getInstance().getConfig();

        if (tConfig.getActive() && (v == punchButtonOne || v == punchButtonTwo)) {
            RequestBody tbRequestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("secret", secret)
                    .addFormDataPart("member", "" + member.getId())
                    .addFormDataPart("question", tConfig.getId())
                    .addFormDataPart("response", v == punchButtonOne ? "a" : "b")
                    .build();

            Request tbRequest = new Request.Builder()
                    .url(getString(R.string.api_base) + getString(R.string.api_teambuilding_respond))
                    .post(tbRequestBody)
                    .build();

            client.newCall(tbRequest).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("Teambuilding Submission", "Error submitting teambuilding", e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        Log.e("Teambuilding Submission", "Non-200 response submitting teambuilding: " + response.toString());
                    }
                }
            });
        }
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
                    HoursResponse hoursResponse = new Gson().fromJson(response.body().string(), HoursResponse.class);
                    updateUiWithHoursResponse(hoursResponse);
                } else {
                    Log.e("Hours Response", "Non-200 response: " + response.toString());
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
                    signedInResponse = new Gson().fromJson(response.body().string(), SignedInResponse.class);
                    updateUiWithSignedInResponse(signedInResponse);
                } else {
                    Log.e("Punch Status Response", "Non-200 response: " + response.toString());
                }
            }
        });
    }

    private void updateUiWithPunchResponse(PunchResponse response) {
        activity.runOnUiThread(() -> {
            if (response.getSuccess()) {
                punchResults.setText(String.format(getString(R.string.punch_summary_format),
                        response.getMember(), response.getPunch(), response.getTime()));
            } else {
                punchResults.setText(response.getError());
            }

            if (response.getDuration() != null) {
                punchDuration.setText(String.format(getString(R.string.punch_duration_format),
                        response.getDuration()));
                punchDuration.setVisibility(View.VISIBLE);
            }

            actionContainer.setVisibility(View.GONE);
            resultsContainer.setVisibility(View.VISIBLE);

            activity.getMembers();
        });

        getStudentHours();
    }

    private void updateUiWithHoursResponse(HoursResponse hoursResponse) {
        activity.runOnUiThread(() -> {
            totals.setText(hoursResponse.toString());
            divider.setVisibility(View.VISIBLE);
            totalsContainer.setVisibility(View.VISIBLE);
        });
    }

    private void updateUiWithSignedInResponse(SignedInResponse signedInResponse) {
        activity.runOnUiThread(() -> {
            TeambuildingResponse tConfig = TeambuildingUtils.getInstance().getConfig();

            if (tConfig != null && tConfig.getActive() && !signedInResponse.getIsSignedIn()) {
                tbQuestion.setText(tConfig.getQuestion());
                punchButtonOne.setText(tConfig.getOption_one());
                punchButtonTwo.setText(tConfig.getOption_two());

                tbInstructions.setVisibility(View.VISIBLE);
                tbQuestion.setVisibility(tConfig.getQuestion() == null || tConfig.getQuestion().trim().isEmpty() ? View.GONE : View.VISIBLE);
                tbOrText.setVisibility(View.VISIBLE);
                punchButtonTwo.setVisibility(View.VISIBLE);
                actionContainer.setVisibility(View.VISIBLE);
            } else {
                punchButtonOne.setText(signedInResponse.getIsSignedIn() ? "Punch Out" : "Punch In");
                punchButtonTwo.setVisibility(View.GONE);
                actionContainer.setVisibility(View.VISIBLE);
            }
        });
    }
}
