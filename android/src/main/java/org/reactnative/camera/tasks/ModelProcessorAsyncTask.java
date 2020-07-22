package org.reactnative.camera.tasks;

import org.reactnative.camera.RNCameraViewHelper;
import org.reactnative.camera.tensorflow.pose.Keypoint;
import org.reactnative.camera.tensorflow.pose.Person;
import org.tensorflow.lite.Interpreter;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.view.TextureView;

import com.google.android.cameraview.CameraView;

import java.nio.ByteBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ModelProcessorAsyncTask extends android.os.AsyncTask<Void, Void, ByteBuffer> {
  private static final float INPUT_MEAN = 128.0F;
  private static final float INPUT_STD = 128.0F;

  private CameraView mView;
  private Interpreter mModelProcessor;
  private ByteBuffer mModelInput;
  private ByteBuffer mModelOutput;
  private int[] mModelViewBuf;
  private int mWidth;
  private int mHeight;
  private int mRotation;

  public ModelProcessorAsyncTask(
          CameraView view,
          Interpreter modelProcessor,
          ByteBuffer inputBuf,
          ByteBuffer outputBuf,
          int[] modelViewBuf,
          int width,
          int height,
          int rotation
  ) {
    mView = view;
    mModelProcessor = modelProcessor;
    mModelInput = inputBuf;
    mModelOutput = outputBuf;
    mModelViewBuf = modelViewBuf;
    mWidth = width;
    mHeight = height;
    mRotation = rotation;
  }

  private void updateImage() {
    final TextureView texture = (TextureView) mView.getView();
    final Bitmap bitmap = texture.getBitmap(mWidth, mHeight);

    if (bitmap == null) {
      return;
    }

    mModelInput.rewind();
    bitmap.getPixels(mModelViewBuf, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

    for (int pixel : mModelViewBuf) {
      mModelInput.putFloat((((pixel >> 16) & 0xFF) - INPUT_MEAN) / INPUT_STD);
      mModelInput.putFloat((((pixel >> 8) & 0xFF) - INPUT_MEAN) / INPUT_STD);
      mModelInput.putFloat(((pixel & 0xFF) - INPUT_MEAN) / INPUT_STD);
    }
  }

  @Override
  protected ByteBuffer doInBackground(Void... ignored) {
    if (isCancelled() || mModelProcessor == null || (!(mView instanceof ModelProcessorAsyncTaskDelegate))) {
      return null;
    }

    try {
      updateImage();
      runTFLiteModel();
    } catch (Exception e) {
      return null;
    }

    return mModelOutput;
  }

  private Map<Integer, Object> initOutputMap() {
    final Map<Integer, Object> map = new HashMap<>();

    for (int index = 0; index < 4; index++) {
      final int[] shape = mModelProcessor.getOutputTensor(index).shape();
      map.put(index, new float[shape[0]][shape[1]][shape[2]][shape[3]]);
    }

    return map;
  }

  public void runTFLiteModel() {
    final Map<Integer, Object> map = initOutputMap();
    final ByteBuffer[] inputArr = new ByteBuffer[1];
    final long start = SystemClock.currentThreadTimeMillis();
    inputArr[0] = mModelInput;

    mModelInput.rewind();
    mModelProcessor.runForMultipleInputsOutputs(inputArr, map);

    final Person person = Person.getPerson(map, mWidth, mHeight);
    final long inference = SystemClock.currentThreadTimeMillis() - start;

    RNCameraViewHelper.emitModelPoseProcessedEvent(mView, person, inference);

//    for (Keypoint key : person.keypoints) {
//      Log.i("DEBUG", key.bodyPart.toString() + "[" + key.score + "]: " + "{" + key.position.x + ", " + key.position.y + "}");
//    }
//    Log.i("DEBUG", "Score: " + person.score);
  }

  @Override
  protected void onPostExecute(ByteBuffer data) {
    super.onPostExecute(data);

    final ModelProcessorAsyncTaskDelegate delegate = (ModelProcessorAsyncTaskDelegate) mView;

    if (data != null) {
      delegate.onModelProcessed(data, mWidth, mHeight, mRotation);
    }

    delegate.onModelProcessorTaskCompleted();
  }
}
