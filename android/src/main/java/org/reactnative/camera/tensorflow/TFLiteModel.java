package org.reactnative.camera.tensorflow;

import android.content.Context;
import android.util.Log;

import com.google.android.cameraview.CameraView;

import org.reactnative.camera.RNCameraViewHelper;
import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TFLiteModel {

    private final static int FLOAT_SIZE = Float.SIZE / Byte.SIZE;
    private Interpreter interpreter;
    private Dimension inputDimension;
    private int outputSize;

    public TFLiteModel(
            Context context,
            String fileName,
            Dimension inputDimension,
            int[] outputShape
    ){
        this.inputDimension = inputDimension;
        this.outputSize = getSize(outputShape);
        interpreter = loadModelFromAssets(context, fileName);
    }

    private Interpreter loadModelFromAssets(Context ctx, String fileName){
        try {
            InputStream inputStream = ctx.getAssets().open(fileName);
            byte[] model = new byte[inputStream.available()];
            inputStream.read(model);
            ByteBuffer buffer = ByteBuffer.allocateDirect(model.length)
                    .order(ByteOrder.nativeOrder());
            buffer.put(model);
            Log.e("success", "Model found");
            return new Interpreter(buffer);
        }catch (IOException e){
            Log.e("error", "Model not found");
            return null;
        }
    }

    public void run(CameraView view, byte[] data) {
        RNCameraViewHelper.emitModelProcessedEvent(view, data);
    }

    private static int getSize(int[] shape) {
        int result = 1;
        for (int dimension: shape)
            result *= dimension;
        return result;
    }
}

