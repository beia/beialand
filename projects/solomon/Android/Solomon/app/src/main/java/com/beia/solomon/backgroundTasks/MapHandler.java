package com.beia.solomon.backgroundTasks;

import android.os.Handler;
import android.os.Message;

import com.beia.solomon.fragments.MapFragment;
import com.beia.solomon.model.Mall;

import java.util.List;

public class MapHandler extends Handler {
    private MapFragment mapFragment;

    public MapHandler(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                mapFragment.updateParkingSpaces((List<Mall>) msg.obj);
                break;
        }
    }
}
