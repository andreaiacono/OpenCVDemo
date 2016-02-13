package org.opencv.demo.gui.swing;

import org.opencv.demo.misc.ImageUtils;
import org.opencv.demo.misc.Loggable;
import org.opencv.demo.core.DetectorsManager;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class WebcamPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private BufferedImage imageToDisplay;
    private Loggable logger;
    private DetectorsManager detectorsManager;

    public WebcamPanel(Loggable logger, DetectorsManager detectorsManager) {
        this.logger = logger;
        this.detectorsManager = detectorsManager;
    }

    public void startCamera() {
        new PanelUpdater().execute();
        logger.log("Started camera.");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageToDisplay == null) {
            return;
        }
        g.drawImage(this.imageToDisplay, 1, 1, this.imageToDisplay.getWidth(), this.imageToDisplay.getHeight(), null);
    }

    public void updateImage(Mat image) {

        imageToDisplay = ImageUtils.getBufferedImageFromMat(image);
        repaint();
    }

    public void addClassifier(String classifierName) {
        detectorsManager.addDetector(classifierName);
    }

    public void removeClassifier(String classifierName) {
        detectorsManager.removeDetector(classifierName);
    }

    private class PanelUpdater extends SwingWorker {

        private final VideoCapture camera;
        private Mat capturedImage = new Mat();

        public PanelUpdater() {

            // starts the webcam and sets its resolution
            camera = new VideoCapture(0);
            camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 352);
            camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 288);
        }

        @Override
        protected Object doInBackground() throws Exception {

            while (! isCancelled()) {

                // reads images from webcam
                camera.read(capturedImage);

                if (!capturedImage.empty()) {

                    // get the image potentially transformed by one or more detectors
                    capturedImage = detectorsManager.detect(capturedImage);

                    // and displays it in the panel
                    WebcamPanel.this.updateImage(capturedImage);
                }
            }
          return null;
        }
    }
}
