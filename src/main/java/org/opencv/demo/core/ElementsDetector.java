package org.opencv.demo.core;

import org.opencv.core.*;
import org.opencv.demo.misc.Loggable;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An ElementsDetector uses a cascade classifier to find one or more elements
 * inside an image.
 */
public class ElementsDetector {

    private List<DetectedElement> detectedElements;
    private CascadeClassifier cascadeClassifier;
    private String detectorName;
    private Scalar color;
    private Loggable logger;

    public ElementsDetector(String detectorName, Loggable logger, Scalar color) {
        this.detectorName = detectorName;
        this.logger = logger;
        this.color = color;
        URL detectorData = getClass().getResource(detectorName);
        if (detectorData == null) {
            logger.log("Could not load cascade data [" + detectorName + "]: file not found.");
        } else {
            cascadeClassifier = new CascadeClassifier(detectorData.getPath());
            logger.log(cascadeClassifier.empty() ? "Could not create cascade classifier from [" + detectorName + "]." : "Cascade classifier [" + detectorName + "] successfully loaded.");
        }
    }

    public List<DetectedElement> detectElements(Mat inputFrame) {

        detectedElements = new ArrayList<>(10);
        Mat mRgba = new Mat();
        Mat mGrey = new Mat();
        inputFrame.copyTo(mRgba);
        inputFrame.copyTo(mGrey);
        Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(mGrey, mGrey);
        MatOfRect results = new MatOfRect();
        cascadeClassifier.detectMultiScale(mGrey, results);
        Rect[] classifiedElements = results.toArray();

        for (Rect rect : classifiedElements) {

            int size = (int) (rect.width / 5f);

            // draws a frame around the detected element
            drawLine(mRgba, rect.x - 1, rect.y - 1, rect.x + size, rect.y - 1);
            drawLine(mRgba, rect.x - 1, rect.y - 1, rect.x - 1, rect.y + size);
            drawLine(mRgba, rect.x + rect.width - 1, rect.y - 1, rect.x + rect.width - size, rect.y - 1);
            drawLine(mRgba, rect.x + rect.width - 1, rect.y - 1, rect.x + rect.width - 1, rect.y + size);
            drawLine(mRgba, rect.x - 1, rect.y + rect.height, rect.x - 1 + size, rect.y + rect.height);
            drawLine(mRgba, rect.x - 1, rect.y + rect.height, rect.x -1, rect.y + rect.width - size);
            drawLine(mRgba, rect.x + rect.width, rect.y + rect.height, rect.x + rect.width - size, rect.y + rect.height);
            drawLine(mRgba, rect.x + rect.width, rect.y + rect.height, rect.x + rect.width, rect.y + rect.width - size);

            // and adds it to the
            detectedElements.add(new DetectedElement(mRgba, new Mat(mRgba.clone(), rect), new Point(rect.x, rect.y)));
        }

        return detectedElements;
    }

    private void drawLine(Mat image, int x1, int y1, int x2, int y2) {
        Imgproc.line(
                image,
                new Point(x1, y1),
                new Point(x2, y2),
                color,
                1);
    }

    public List<DetectedElement> getDetectedElements() {
        return detectedElements;
    }

    public String getDetectorName() {
        return detectorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementsDetector that = (ElementsDetector) o;
        return detectorName.equals(that.detectorName);
    }

    @Override
    public int hashCode() {
        return detectorName.hashCode();
    }
}
