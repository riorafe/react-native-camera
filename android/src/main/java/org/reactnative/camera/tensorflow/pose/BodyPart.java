package org.reactnative.camera.tensorflow.pose;

public enum BodyPart {
    NOSE("nose"),
    LEFT_EYE("left_eye"),
    RIGHT_EYE("right_eye"),
    LEFT_EAR("left_ear"),
    RIGHT_EAR("right_ear"),
    LEFT_SHOULDER("left_shoulder"),
    RIGHT_SHOULDER("right_shoulder"),
    LEFT_ELBOW("left_elbow"),
    RIGHT_ELBOW("right_elbow"),
    LEFT_WRIST("left_wrist"),
    RIGHT_WRIST("right_wrist"),
    LEFT_HIP("left_hip"),
    RIGHT_HIP("right_hip"),
    LEFT_KNEE("left_knee"),
    RIGHT_KNEE("right_knee"),
    LEFT_ANKLE("left_ankle"),
    RIGHT_ANKLE("right_angkle");

    private final String key;

    BodyPart(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
