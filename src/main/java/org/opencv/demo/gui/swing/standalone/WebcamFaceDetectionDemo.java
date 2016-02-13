package org.opencv.demo.gui.swing.standalone;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URL;

public class WebcamFaceDetectionDemo extends JFrame {

    private WebcamPanel webcamPanel;
    private CascadeClassifier face_cascade;
    private VideoCapture camera;
    private BufferedImage image;

    public WebcamFaceDetectionDemo() {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        webcamPanel = new WebcamPanel();
        add(webcamPanel);
        setupOpenCv();

        setVisible(true);
    }

    public void setupOpenCv() {
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("Camera was not opened.");
        }

        URL cascadeData = getClass().getResource("/haarcascade_frontalface_alt.xml");
        if (cascadeData == null) {
            System.err.println("Could not load cascade data: file not found.");
            System.exit(-1);
        }
        face_cascade = new CascadeClassifier(cascadeData.getPath());
        new PanelUpdater().execute();
    }


    public Mat detect(Mat inputFrame) {
        Mat mRgba = new Mat();
        Mat mGrey = new Mat();
        MatOfRect faces = new MatOfRect();
        inputFrame.copyTo(mRgba);
        inputFrame.copyTo(mGrey);
        Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(mGrey, mGrey);

        face_cascade.detectMultiScale(mGrey, faces);
        Rect[] faceArray = faces.toArray();
        for (Rect rect : faceArray) {
            Imgproc.rectangle(
                    mRgba,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0),
                    1);
        }
        return mRgba;
    }


    private class WebcamPanel extends JPanel {

        public void updateImage(Mat matrix) {

            int type = BufferedImage.TYPE_BYTE_GRAY;
            if (matrix.channels() > 1) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            byte[] b = new byte[matrix.channels() * matrix.cols() * matrix.rows()];
            matrix.get(0, 0, b);
            image = new BufferedImage(matrix.cols(), matrix.rows(), type);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(b, 0, targetPixels, 0, b.length);

            repaint();
        }
    }

    private class PanelUpdater extends SwingWorker {
        @Override
        protected Void doInBackground() throws Exception {
            Mat webcamImage = new Mat();
            while (!isCancelled()) {
                camera.read(webcamImage);
                if (!webcamImage.empty()) {
                    Mat detectedFacesImage = detect(webcamImage);
                    webcamPanel.updateImage(detectedFacesImage);
                } else {
                    break;
                }
            }
            return null;
        }
    }


    public static void main(String[] args) {
        new WebcamFaceDetectionDemo();
    }

}
