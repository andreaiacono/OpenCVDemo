package org.opencv.demo.misc;

import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.demo.core.RecognizedFace;

public class Constants {

    public static final String OPENCVDEMO = "OpenCv Demo";
    public static final float VERSION = 0.1f;
    public static final String OPENCVDEMO_COMPLETE = OPENCVDEMO + " v" + VERSION;

    public static final int MINIMUM_TRAIN_SET_SIZE = 20;
    public static final String CLASSIFIERS_PATH = "data";
    public static final String TRAINING_FACES_PATH = "faces";
    public static int TRAIN_FACE_IMAGE_HEIGHT = 140;
    public static int TRAIN_FACE_IMAGE_WIDTH = TRAIN_FACE_IMAGE_HEIGHT;
    public static Size TRAIN_FACE_IMAGE_SIZE = new Size( (double) TRAIN_FACE_IMAGE_HEIGHT, (double)TRAIN_FACE_IMAGE_HEIGHT);
    public static double FACE_RECOGNITION_THRESHOLD = 1000;

    public static int MAX_IMAGES_NUMBER_FOR_TRAINING = 50;
    public static String DEFAULT_FACE_CLASSIFIER = "/data/haarcascades/haarcascade_frontalface_alt.xml";

    public static Scalar WHITE = new Scalar(255,255,255);
    public static Scalar BLACK = new Scalar(0,0,0);

    public static float RECOGNIZED_NAME_FONT_SIZE = 0.5f;
    public static final String NOT_RECOGNIZED_FACE = "unknown";
    public static final RecognizedFace UNKNOWN_FACE = new RecognizedFace(NOT_RECOGNIZED_FACE, 0d);
}
