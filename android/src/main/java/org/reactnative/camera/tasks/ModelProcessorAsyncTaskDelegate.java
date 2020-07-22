package org.reactnative.camera.tasks;

import com.facebook.react.bridge.WritableArray;

import org.reactnative.facedetector.RNFaceDetector;

import java.nio.ByteBuffer;

public interface ModelProcessorAsyncTaskDelegate {
  void onModelProcessed(ByteBuffer data, int sourceWidth, int sourceHeight, int sourceRotation);
  void onModelProcessorTaskCompleted();
}
