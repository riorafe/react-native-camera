package org.reactnative.camera.tensorflow.pose;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Person {
    public List<Keypoint> keypoints = new ArrayList<>();
    public Float score = 0.0f;

    private static float sigmoid (float x){
        return (float) (1.0f / (1.0f + Math.exp(-x)));
    }

    public static Person getPerson(Map<Integer, Object> outputMap, int imageWidth, int imageHeight) {
        Object heatmaps = outputMap.get(0);
        float[][][][] heatmapsFloat = (float[][][][])heatmaps;
        Object offsets = outputMap.get(1);
        float[][][][] offsetsFloat = (float[][][][])offsets;

        int height = heatmapsFloat[0].length;
        int width = heatmapsFloat[0][0].length;
        int numKeypoints = heatmapsFloat[0][0][0].length;

        List<Pair<Integer, Integer>> keypointPositions = new ArrayList<>();
        for (int keypoint = 0; keypoint < numKeypoints; keypoint++){
            float maxVal = heatmapsFloat[0][0][0][keypoint];
            int maxRow = 0;
            int maxCol = 0;
            for (int row = 0; row < height; row++){
                for (int col = 0; col < width; col++){
                    if (heatmapsFloat[0][row][col][keypoint] > maxVal) {
                        maxVal = heatmapsFloat[0][row][col][keypoint];
                        maxRow = row;
                        maxCol = col;
                    }
                }
            }
            keypointPositions.add(keypoint, new Pair(maxRow, maxCol));
        }
        int[] xCoords = new int[numKeypoints];
        int[] yCoords = new int[numKeypoints];
        float[] confidenceScores = new float[numKeypoints];

        for (int i = 0; i < keypointPositions.size(); i++) {
            int positionY = keypointPositions.get(i).first;
            int positionX = keypointPositions.get(i).second;
            yCoords[i] = (int) (keypointPositions.get(i).first/ (float)(height-1) * imageHeight + offsetsFloat[0][positionY][positionX][i]);
            xCoords[i] = (int) (keypointPositions.get(i).second/ (float)(width-1) * imageWidth + offsetsFloat[0][positionY][positionX][i + numKeypoints]);
            confidenceScores[i] = sigmoid(heatmapsFloat[0][positionY][positionX][i]);
        }

        Person person = new Person();
        List<Keypoint> keypointList = new ArrayList<>();
        float totalScore = 0.0f;

        int i = 0;
        for (BodyPart part : BodyPart.values()) {
            keypointList.add(i, new Keypoint());
            keypointList.get(i).bodyPart = part;
            keypointList.get(i).position.x = xCoords[i];
            keypointList.get(i).position.y = yCoords[i];
            keypointList.get(i).score = confidenceScores[i];
            totalScore += confidenceScores[i];
            i++;
        }
        person.keypoints = keypointList;
        person.score = totalScore / numKeypoints;

        return person;
    }
}