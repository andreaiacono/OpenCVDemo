package org.opencv.demo.core;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
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
import java.net.URL;
import java.util.*;

public class RecognizerManager {

    private final List images;
    private final MatOfInt labelsBuffer;
    private FaceRecognizer faceRecognizer;
    private Loggable logger;
    Map<Integer, String> idToNameMapping = null;
    private int[] labels;
    private double[] confidence;



    public RecognizerManager(Loggable logger) throws Exception {
        this.logger = logger;

        URL dir_url = ClassLoader.getSystemResource(Constants.TRAINING_FACES_PATH);
        File trainingDir = new File(dir_url.toURI());

        File[] imageFiles = getImagesFiles(trainingDir);
        idToNameMapping = createSummary(imageFiles);

        images = new ArrayList(imageFiles.length);
        labelsBuffer = new MatOfInt(new int[imageFiles.length]);

        int counter = 0;
        for (File image : imageFiles) {

            // reads the training image in grayscale
            Mat img = Imgcodecs.imread(image.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

            // gets the id of this image
            int labelId = getIdFromImage(image.getName(), idToNameMapping);

            // sets the image
            images.add(img);
            labelsBuffer.put(counter++, 0, labelId);
        }

        faceRecognizer = RecognizerFactory.getRecognizer(RecognizerType.FISHER);
        trainRecognizer();
        labels = new int[idToNameMapping.size()];
        confidence = new double[idToNameMapping.size()];
    }

    public void trainRecognizer() {
        faceRecognizer.train(images, labelsBuffer);
    }

    public void changeRecognizer(FaceRecognizer faceRecognizer) {
        this.faceRecognizer = faceRecognizer;
        trainRecognizer();
    }

    public RecognizedFace recognizeFace(Mat face) {

        if (face == null) {
            return Constants.UNKNOWN_FACE;
        }

        Mat resizedGrayFace = ImageUtils.toGrayScale(ImageUtils.resizeFace(face));
        faceRecognizer.predict(resizedGrayFace, labels, confidence);

        if (confidence[0] < Constants.FACE_RECOGNITION_THRESHOLD) {
            return new RecognizedFace(idToNameMapping.get(labels[0]), confidence[0]);
        }

        return Constants.UNKNOWN_FACE;
    }

    private File[] getImagesFiles(File trainingDir) {

        FilenameFilter imgFilter = (dir, name) -> {
            name = name.toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
        };

        return trainingDir.listFiles(imgFilter);
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
