package org.reactnative.camera.events;

import androidx.core.util.Pools;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.reactnative.camera.CameraViewManager;

import java.util.Date;

public class ModelProcessedEvent extends Event<ModelProcessedEvent> {
    private static final CameraViewManager.Events EVENT = CameraViewManager.Events.EVENT_ON_MODEL_PROCESSED;
    private static final Pools.SynchronizedPool<ModelProcessedEvent> EVENTS_POOL =
            new Pools.SynchronizedPool<>(3);

    private byte[] data;

    private ModelProcessedEvent() {}

    public static ModelProcessedEvent obtain(int viewTag, byte[] data) {
        ModelProcessedEvent event = EVENTS_POOL.acquire();
        if (event == null) {
            event = new ModelProcessedEvent();
        }
        event.init(viewTag, data);
        return event;
    }

    private void init(int viewTag, byte[] data) {
        super.init(viewTag);
        this.data = data;
    }

    @Override
    public String getEventName() {
        return EVENT.toString();
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
    }

    private WritableMap serializeEventData() {
        WritableMap event = Arguments.createMap();

        event.putString("data", this.data.toString());

        return event;
    }
}
