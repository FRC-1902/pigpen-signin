package com.explodingbacon.pigpen.signin.utils;

import android.content.Context;
import android.util.Log;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.api.models.TeambuildingResponse;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeambuildingUtils {
    private static TeambuildingUtils instance;

    private static Context context;
    private TeambuildingResponse config;

    public static TeambuildingUtils getInstance() {
        if (instance == null) {
            instance = new TeambuildingUtils();
            instance.refresh();
        }

        return instance;
    }

    public static void init(Context ctx) {
        context = ctx;
    }

    public void refresh() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(context.getString(R.string.api_base) + context.getString(R.string.api_teambuilding_config))
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
                    synchronized (this) {
                        config = new Gson().fromJson(response.body().string(), TeambuildingResponse.class);
                    }
                } else {
                    Log.e("Teambuilding Response", "Non-200 response: " + response.toString());
                }
            }
        });
    }

    public TeambuildingResponse getConfig() {
        synchronized (this) {
            return config;
        }
    }
}
