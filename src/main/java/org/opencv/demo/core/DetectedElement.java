package org.opencv.demo.core;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class DetectedElement {

    private Mat transformedImage;
    private Mat detectedImageElement;
    private Point position;

    public DetectedElement(Mat sourceImage, Mat detectedImageElement, Point position) {

        this.transformedImage = sourceImage;
        this.detectedImageElement = detectedImageElement;
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public Mat getTransformedImage() {
        return transformedImage;
    }

    public Mat getDetectedImageElement() {
        return detectedImageElement;
    }
}
