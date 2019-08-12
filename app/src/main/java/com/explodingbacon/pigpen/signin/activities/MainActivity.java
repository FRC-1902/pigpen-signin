package com.explodingbacon.pigpen.signin.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.adapters.MemberListAdapter;
import com.explodingbacon.pigpen.signin.adapters.MemberListPager;
import com.explodingbacon.pigpen.signin.api.models.MemberResponse;
import com.explodingbacon.pigpen.signin.beans.Member;
import com.explodingbacon.pigpen.signin.fragments.AddMemberFragment;
import com.explodingbacon.pigpen.signin.fragments.ApiKeyFragment;
import com.explodingbacon.pigpen.signin.fragments.PunchFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

    MemberListPager pagerAdapter;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_REPO_NAME, MODE_PRIVATE);
        if (!prefs.contains(PREFS_KEY_APIKEY)) {
            new ApiKeyFragment().show(getSupportFragmentManager(), FRAGMENT_API);
        }

        pagerAdapter = new MemberListPager(getSupportFragmentManager(), this);

        ViewPager pager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getMembers();
            }
        }, 0, TimeUnit.MINUTES.toMillis(1));
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

    public void getMembers() {
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
                pagerAdapter.updateMembers(gson.fromJson(response.body().string(), MemberResponse.class).getMembers());
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
