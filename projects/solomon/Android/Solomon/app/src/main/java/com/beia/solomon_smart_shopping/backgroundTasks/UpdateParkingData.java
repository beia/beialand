package com.beia.solomon_smart_shopping.backgroundTasks;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.beia.solomon_smart_shopping.GsonRequest;
import com.beia.solomon_smart_shopping.R;
import com.beia.solomon_smart_shopping.fragments.MapFragment;
import com.beia.solomon_smart_shopping.model.Mall;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateParkingData implements Runnable {
    private Context context;
    private RequestQueue volleyQueue;

    public UpdateParkingData(Context context, RequestQueue volleyQueue) {
        this.context = context;
        this.volleyQueue = volleyQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                getMalls();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getMalls() {
        String url = context.getResources().getString(R.string.getMallsUrl);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", context.getResources().getString(R.string.universal_user));

        GsonRequest<List> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                List.class,
                headers,
                response -> {
                    Log.d("MALLS", "malls");
                    Gson gson = new Gson();
                    List<Mall> malls = gson.fromJson(gson.toJson(response), new TypeToken<List<Mall>>(){}.getType());
                    sendMallsToUIThread(malls);
                },
                error -> {
                    if(error.networkResponse != null && error.networkResponse.data != null)
                        Log.d("ERROR", "getMalls: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }

    private void sendMallsToUIThread(List<Mall> malls) {
        Message message = MapFragment.mapHandler.obtainMessage();
        message.what = 0;
        message.obj = malls;
        message.sendToTarget();
    }
}
