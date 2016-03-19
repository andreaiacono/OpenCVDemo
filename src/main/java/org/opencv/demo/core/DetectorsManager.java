package org.opencv.demo.core;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.demo.misc.Constants;
import org.opencv.demo.misc.Loggable;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class DetectorsManager {

    private Scalar[] colors;
    private Loggable logger;
    private Map<String, ElementsDetector> detectors = new HashMap<>();
    private RecognizerManager recognizerManager;
    int counter = 0;
    private boolean isRecognizerActive = false;

    public DetectorsManager(RecognizerManager recognizerManager, Loggable logger) {
        this.recognizerManager = recognizerManager;
        this.logger = logger;
        init();
    }

    public DetectorsManager(Loggable logger) throws Exception {
        this.logger = logger;
        this.recognizerManager = new RecognizerManager(logger);
        init();
    }

    public void init() {

        // defines colors for drawing the frame for detected elements
        colors = new Scalar[] {
                new Scalar(255, 0, 0),
                new Scalar(0, 255, 0),
                new Scalar(0, 0, 255),
                new Scalar(255, 0, 255),
                new Scalar(255, 255, 0),
                new Scalar(0, 255, 255),
                new Scalar(100, 100, 100),
                new Scalar(200, 200, 200)
        };
    }

    public Mat detect(Mat capturedImage) {

        List<DetectedElement> detectedElements;

        // loops over all the activated detectors
        for (ElementsDetector detector: detectors.values()) {

            // gets the elements detected by this detector
            detectedElements = detector.detectElements(capturedImage);
            for (DetectedElement detectedElement: detectedElements) {

                // gets the image transformed by the detector
                capturedImage = detectedElement.getTransformedImage();

                // if has to recognize a face
                if (isRecognizerActive && detectedElement != null && detectedElement.getDetectedImageElement() != null) {

                    assert(detectors.size() == 1 && detectors.containsKey(Constants.DEFAULT_FACE_CLASSIFIER));

                    // recognizes the face
                    RecognizedFace recognizedFace = recognizerManager.recognizeFace(detectedElement.getDetectedImageElement());
                    String name;
                    if (recognizedFace == Constants.UNKNOWN_FACE) {
                        name = recognizedFace.getName();
                    }
                    else {
                        int percentage = (int)(100 * (Constants.FACE_RECOGNITION_THRESHOLD - recognizedFace.getConfidence()) / Constants.FACE_RECOGNITION_THRESHOLD);
                        name = recognizedFace.getName() + "  [" + percentage + "%]";
                    }

                    // writes the name of the recognized person (sort of embossed)
                    Point position = detectedElement.getPosition();
                    position.y -= 11;
                    position.x -= 1;
                    Imgproc.putText(capturedImage, name, position, Core.FONT_HERSHEY_TRIPLEX, Constants.RECOGNIZED_NAME_FONT_SIZE, Constants.BLACK);

                    position.y += 1;
                    position.x += 1;
                    Imgproc.putText(capturedImage, name, position, Core.FONT_HERSHEY_TRIPLEX, Constants.RECOGNIZED_NAME_FONT_SIZE, colors[2]);
                }
            }
        }

        return capturedImage;
    }

    public void addDetector(String detectorName) {
        counter ++;
        ElementsDetector detector = new ElementsDetector(detectorName, logger, colors[counter%colors.length]);
        detectors.put(detectorName, detector);
    }

    public void removeDetector(String detectorNameToRemove) {
        Iterator<String> iterator = detectors.keySet().iterator();
        while (iterator.hasNext()) {
            String detectorName = iterator.next();
            if (detectorName.equals(detectorNameToRemove)) {
                iterator.remove();
                logger.log("Removed [" + detectorNameToRemove + "].");
            }
        }
    }

    public void changeRecognizer(RecognizerType recognizerType) {
        if (isRecognizerActive) {
            changeRecognizerStatus();
            recognizerManager.changeRecognizer(RecognizerFactory.getRecognizer(recognizerType));
            changeRecognizerStatus();
        }
        else {
            recognizerManager.changeRecognizer(RecognizerFactory.getRecognizer(recognizerType));
        }
    }

    public boolean hasDetector(String detectorName) {
        return detectors.values().stream().anyMatch(d -> d.getDetectorName().equals(detectorName));
    }

    public ElementsDetector getDetector(String name) {
        return detectors.get(name);
    }

    public Collection<ElementsDetector> getDetectors() {
        return detectors.values();
    }

    public void clear() {
        detectors.clear();
    }

    public boolean changeRecognizerStatus() {
        isRecognizerActive = !isRecognizerActive;
        return isRecognizerActive;
    }
}
