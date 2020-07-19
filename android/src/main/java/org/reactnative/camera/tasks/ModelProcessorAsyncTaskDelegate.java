package org.reactnative.camera.tasks;

import com.facebook.react.bridge.WritableArray;

import org.reactnative.facedetector.RNFaceDetector;

public interface ModelProcessorAsyncTaskDelegate {
  void onModelProcessed(byte[] data);
  void onModelProcessComplete();
}
