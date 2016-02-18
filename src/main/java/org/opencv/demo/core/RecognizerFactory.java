package org.opencv.demo.core;

import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;

public class RecognizerFactory {

    public static FaceRecognizer getRecognizer(RecognizerType recognizerType) {

        if (recognizerType == RecognizerType.EIGEN) {
            return Face.createEigenFaceRecognizer();
        }
        if (recognizerType == RecognizerType.FISHER) {
            return Face.createFisherFaceRecognizer();
        }
        if (recognizerType == RecognizerType.LBPH) {
            return Face.createLBPHFaceRecognizer();
        }

        throw new IllegalArgumentException("Recognizer " + recognizerType + " not found.");
    }
}
