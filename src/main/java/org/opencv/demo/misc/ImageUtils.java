package org.opencv.demo.misc;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static Mat resizeFace(Mat originalImage) {
        Mat resizedImage = new Mat();
        Imgproc.resize(originalImage, resizedImage, Constants.TRAIN_FACE_IMAGE_SIZE);
        return resizedImage;
    }

    public static Mat toGrayScale(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        return grayImage;
    }

    public static BufferedImage getBufferedImageFromMat(Mat image) {

        // TODO: avoid this check!
//        int type = BufferedImage.TYPE_BYTE_GRAY;
//        if (image.channels() > 1) {
           int type = BufferedImage.TYPE_3BYTE_BGR;
//        }
        BufferedImage bufferedImage = new BufferedImage(image.cols(), image.rows(), type);
        byte[] b = new byte[image.channels() * image.cols() * image.rows()];
        image.get(0, 0, b);
        final byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);

        return bufferedImage;
    }

    public static void saveAsJpg(Mat face, String filename) {
        try {
            ImageIO.write(ImageUtils.getBufferedImageFromMat(face), "JPG", new File(filename));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
