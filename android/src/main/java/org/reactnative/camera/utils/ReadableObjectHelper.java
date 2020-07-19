package org.reactnative.camera.utils;

import com.facebook.react.bridge.ReadableArray;

public class ReadableObjectHelper {
    public static final int[] getArrayInt(ReadableArray array) {
        final int size = array.size();
        final int[] values = new int[size + 1];

        for (int index = 0; index < size; index++) {
            values[index] = array.getInt(index);
        }

        return values;
    }
}
