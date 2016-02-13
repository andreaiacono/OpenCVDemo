package org.opencv.demo.gui.swing.standalone;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FaceTrainer {

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String trainingDir = "/home/andrea/opencv";

        File[] imageFiles = getImagesFiles(trainingDir);
        Map<Integer, String> idToNameMapping = createSummary(imageFiles);

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

        FaceRecognizer faceRecognizer =  Face.createFisherFaceRecognizer();
//         FaceRecognizer faceRecognizer = Face.createEigenFaceRecognizer();
//         FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();

        faceRecognizer.train(images, labelsBuf);

        // now we recognize a couple of faces
        Mat testImageAndrea = Imgcodecs.imread("/home/andrea/andrea.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat testImageChiarina = Imgcodecs.imread("/home/andrea/chiarina.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        int predictedLabelAndrea = faceRecognizer.predict(testImageAndrea);
        int predictedLabelChiarina = faceRecognizer.predict(testImageChiarina);

        System.out.println("Predicted label andrea: " + idToNameMapping.get(predictedLabelAndrea));
        System.out.println("Predicted label chiarina: " + idToNameMapping.get(predictedLabelChiarina));
    }

    private static File[] getImagesFiles(String trainingDir) {

        File root = new File(trainingDir);

        FilenameFilter imgFilter = (dir, name) -> {
            name = name.toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
        };

        return root.listFiles(imgFilter);
    }

    private static int getIdFromImage(String filename, Map<Integer, String> idToNameMapping) {
        String name = filename.split("_")[0];
        return idToNameMapping.keySet()
                .stream()
                .filter(id -> idToNameMapping.get(id).equals(name))
                .findFirst()
                .orElse(-1);
    }

    private static Map<Integer, String> createSummary(File[] imagesFiles) {

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
