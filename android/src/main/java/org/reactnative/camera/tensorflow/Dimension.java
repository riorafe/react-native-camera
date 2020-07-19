package org.reactnative.camera.tensorflow;

public class Dimension {
    public int width;
    public int height;

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getSize() {
        return width * height;
    }
}
