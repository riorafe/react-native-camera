package org.reactnative.camera.tensorflow.pose;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.ThemedReactContext;

import org.reactnative.camera.tensorflow.Dimension;
import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//TODO Extend TFLiteModel and register instance
public class TFLitePoseModel {
    public final ThemedReactContext mThemedReactContext;
    public final int mModelImageDimX;
    public final int mModelImageDimY;
    public final int[] mModelViewBuf;
    public final Interpreter interpreter;
    public final int outputSize;

    public TFLitePoseModel(
            @NonNull ThemedReactContext context,
            @NonNull String fileName,
            @NonNull int inputWidth,
            @NonNull int inputHeight,
            @NonNull int outputSize
    ){
        this.mThemedReactContext = context;
        this.mModelImageDimX = inputWidth;
        this.mModelImageDimY = inputHeight;
        this.outputSize = outputSize;
        this.interpreter = loadModelFromAssets(context, fileName);
        this.mModelViewBuf = new int[mModelImageDimX * mModelImageDimY];
    }

    public boolean isValid() {
        return this.interpreter != null;
    }

    private Interpreter loadModelFromAssets(@NonNull ThemedReactContext context, @NonNull String fileName){
        try {
            final InputStream inputStream = context.getAssets().open(fileName);
            final byte[] model = new byte[inputStream.available()];
            final ByteBuffer buffer = ByteBuffer.allocateDirect(model.length)
                    .order(ByteOrder.nativeOrder());

            inputStream.read(model);
            buffer.put(model);
            return new Interpreter(buffer);
        }catch (IOException e){
            return null;
        }
    }
}
