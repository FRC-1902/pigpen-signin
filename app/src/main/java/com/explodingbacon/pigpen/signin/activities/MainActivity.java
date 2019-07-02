package com.explodingbacon.pigpen.signin.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.adapters.MemberListAdapter;
import com.explodingbacon.pigpen.signin.api.models.MemberResponse;
import com.explodingbacon.pigpen.signin.beans.Member;
import com.explodingbacon.pigpen.signin.fragments.AddMemberFragment;
import com.explodingbacon.pigpen.signin.fragments.ApiKeyFragment;
import com.explodingbacon.pigpen.signin.fragments.PunchFragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MemberListAdapter.OnMemberClickedListener, AddMemberFragment.MemberAddedListener {
    SharedPreferences prefs;
    public static final String PREFS_REPO_NAME = "PIGPEN";
    public static final String PREFS_KEY_APIKEY = "API_KEY";
    public static final String FRAGMENT_API = "api";
    public static final String FRAGMENT_PUNCH = "punch";
    public static final String FRAGMENT_ADD = "add";

    RecyclerView recycler;
    MemberListAdapter adapter;

    List<Member> members;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_REPO_NAME, MODE_PRIVATE);
        if (!prefs.contains(PREFS_KEY_APIKEY)) {
            new ApiKeyFragment().show(getSupportFragmentManager(), FRAGMENT_API);
        }

        recycler = findViewById(R.id.recycler);
        adapter = new MemberListAdapter(MainActivity.this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getMembers();
            }
        }, 0, TimeUnit.MINUTES.toMillis(1));

        getMembers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.member_add) {
            AddMemberFragment.getInstance(this).show(getSupportFragmentManager(), FRAGMENT_ADD);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMembers() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getString(R.string.api_base) + getString(R.string.api_member_list))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("MainActivity", "API Failure", e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new Gson();
                members = gson.fromJson(response.body().string(), MemberResponse.class).getMembers();

                runOnUiThread(() -> adapter.setMembers(members));
            }
        });
    }

    @Override
    public void onMemberClicked(Member member) {
        PunchFragment.getInstance(member).show(getSupportFragmentManager(), FRAGMENT_PUNCH);
    }

    @Override
    public void onMemberAdded() {
        getMembers();
    }
}
