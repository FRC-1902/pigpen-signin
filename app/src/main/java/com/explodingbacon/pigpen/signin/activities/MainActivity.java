package com.explodingbacon.pigpen.signin.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.adapters.MemberListAdapter;
import com.explodingbacon.pigpen.signin.api.models.MemberResponse;
import com.explodingbacon.pigpen.signin.beans.Member;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    List<Member> members;

    RecyclerView recycler;
    MemberListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recycler);

        makeCall();
    }

    private void makeCall() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getString(R.string.api_base) + "/api/getmembers")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("MainActivity", "API Failure", e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> {
                    Gson gson = new Gson();
                    members = gson.fromJson(response.body().charStream(), MemberResponse.class).getMembers();

                    adapter = new MemberListAdapter(members);
                    recycler.setAdapter(adapter);
                    recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                });
            }
        });
    }

    public static class MembersResponse {

    }
}
