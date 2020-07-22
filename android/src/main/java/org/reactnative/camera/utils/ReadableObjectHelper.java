package org.reactnative.camera.utils;

import com.facebook.react.bridge.ReadableArray;

public class ReadableObjectHelper {
    public static final int getOutputDimension(ReadableArray array) {
        final int size = array.size();
        int value = 1;

        for (int index = 0; index < size; index++) {
             value *= array.getInt(index);
        }

        return value;
    }
}