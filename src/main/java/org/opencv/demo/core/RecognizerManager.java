package org.opencv.demo.core;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.demo.misc.Constants;
import org.opencv.demo.misc.ImageUtils;
import org.opencv.demo.misc.Loggable;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class RecognizerManager {

    private final FaceRecognizer faceRecognizer;
    private Loggable logger;
    Map<Integer, String> idToNameMapping = null;

    public RecognizerManager(Loggable logger) {
        this.logger = logger;

        String trainingDir = "/home/andrea/opencv";

        File[] imageFiles = getImagesFiles(trainingDir);
        idToNameMapping = createSummary(imageFiles);

        ArrayList images = new ArrayList(imageFiles.length);
        MatOfInt labelsBuf = new MatOfInt(new int[imageFiles.length]);

        int counter = 0;
        for (File image : imageFiles) {

            // reads the training image in grayscale
            Mat img = Imgcodecs.imread(image.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

            // gets the id of this image
            int labelId = getIdFromImage(image.getName(), idToNameMapping);

            // sets the image
            images.add(img);
            labelsBuf.put(counter++, 0, labelId);
        }

        faceRecognizer =  Face.createFisherFaceRecognizer();
        faceRecognizer.train(images, labelsBuf);
    }

    public String recognizeFace(Mat face) {

        if (face == null) {
            return Constants.NOT_RECOGNIZED_FACE;
        }

        Mat resizedGrayFace = ImageUtils.toGrayScale(ImageUtils.resizeFace(face));
        int predictedLabel = faceRecognizer.predict(resizedGrayFace);
        return idToNameMapping.get(predictedLabel);
    }

    private File[] getImagesFiles(String trainingDir) {

        File root = new File(trainingDir);

        FilenameFilter imgFilter = (dir, name) -> {
            name = name.toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
        };

        return root.listFiles(imgFilter);
    }

    private int getIdFromImage(String filename, Map<Integer, String> idToNameMapping) {
        String name = filename.split("_")[0];
        return idToNameMapping.keySet()
                .stream()
                .filter(id -> idToNameMapping.get(id).equals(name))
                .findFirst()
                .orElse(-1);
    }

    private Map<Integer, String> createSummary(File[] imagesFiles) {

        Map<Integer, String> idToNameMapping = new HashMap<>();
        int idCounter = 0;
        for (File imageFile : imagesFiles) {
            String name = imageFile.getName().split("_")[0];
            if (!idToNameMapping.values().contains(name)) {
                idToNameMapping.put(idCounter++, name);
            }
        }

        return idToNameMapping;
    }
}
