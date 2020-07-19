package org.reactnative.camera.tasks;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class ModelProcessorAsyncTask extends android.os.AsyncTask<Void, Void, byte[]> {

  private final ModelProcessorAsyncTaskDelegate mDelegate;
  private final byte[] mData;

  public ModelProcessorAsyncTask(ModelProcessorAsyncTaskDelegate delegate, byte[] data) {
    this.mDelegate = delegate;
    this.mData = data;
  }

  @Override
  protected byte[] doInBackground(Void... ignored) {
    if (mDelegate == null) {
      return null;
    }

    return this.mData;
  }

  @Override
  protected void onPostExecute(byte[] result) {
    super.onPostExecute(result);

    if (result != null) {
      mDelegate.onModelProcessed(result);
    }
    mDelegate.onModelProcessComplete();
  }
}
