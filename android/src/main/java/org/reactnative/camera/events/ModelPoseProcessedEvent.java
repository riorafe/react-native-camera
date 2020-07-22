package org.reactnative.camera.events;

import androidx.annotation.NonNull;
import androidx.core.util.Pools;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.reactnative.camera.CameraViewManager;
import org.reactnative.camera.tensorflow.pose.Keypoint;
import org.reactnative.camera.tensorflow.pose.Person;

public class ModelPoseProcessedEvent extends Event<ModelPoseProcessedEvent> {
    private static final CameraViewManager.Events EVENT = CameraViewManager.Events.EVENT_ON_MODEL_PROCESSED;
    private static final Pools.SynchronizedPool<ModelPoseProcessedEvent> EVENTS_POOL =
            new Pools.SynchronizedPool<>(3);

    private Person person;
    private long inference;

    private ModelPoseProcessedEvent() {}

    public static ModelPoseProcessedEvent obtain(int viewTag, Person person, long inference) {
        ModelPoseProcessedEvent event = EVENTS_POOL.acquire();
        if (event == null) {
            event = new ModelPoseProcessedEvent();
        }
        event.init(viewTag, person, inference);
        return event;
    }

    private void init(int viewTag, Person person, long inference) {
        super.init(viewTag);
        this.person = person;
        this.inference = inference;
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
        final WritableMap event = Arguments.createMap();
        final WritableMap body = Arguments.createMap();

        for (Keypoint keypoint : this.person.keypoints) {
           final WritableMap keyMap = Arguments.createMap();
           final WritableMap keyPosition = Arguments.createMap();

           keyPosition.putInt("x", keypoint.position.x);
           keyPosition.putInt("y", keypoint.position.y);
           keyMap.putDouble("score", keypoint.score);
           keyMap.putMap("position", keyPosition);
           body.putMap(keypoint.bodyPart.getKey(), keyMap);
        }

        event.putMap("body", body);
        event.putDouble("score", this.person.score);
        event.putDouble("inference", this.inference / 1000D);

        return event;
    }
}
