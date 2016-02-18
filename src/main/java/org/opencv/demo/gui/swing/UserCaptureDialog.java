package org.opencv.demo.gui.swing;

import org.opencv.demo.misc.Constants;
import org.opencv.demo.core.DetectorsManager;
import org.opencv.demo.core.ElementsDetector;
import org.opencv.core.Mat;
import org.opencv.demo.misc.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserCaptureDialog extends JDialog {

    private static final String LABEL_START_BUTTON = "Start capturing";
    private static final String LABEL_RESUME_CAPTURING = "Resume capturing";
    private static final String LABEL_STOP_BUTTON = "Stop capturing";
    private static final String LABEL_SINGLE_SHOT = "Single shot";
    private static final String LABEL_TRAIN_BUTTON = "Save images";
    private static final String LABEL_CLOSE_BUTTON = "Close";
    private final JButton startButton, stopButton;
    private final JLabel statusBar = new JLabel(" Ready");;
    private final JScrollPane scrollPane;
    private final JTextField userField;
    private final JButton trainButton;

    private JPanel capturedImagesPanel = new JPanel() {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(capturedFaces.size() * (Constants.TRAIN_FACE_IMAGE_WIDTH + 10), Constants.TRAIN_FACE_IMAGE_HEIGHT + 10);
        }
    };

    private PictureGrabber pictureGrabber;
    private DetectorsManager detectorsManager;
    private BufferedImage image;
    private List<BufferedImage> capturedFaces = new ArrayList();

    public UserCaptureDialog(DetectorsManager detectorsManager) {
        this.detectorsManager = detectorsManager;

        super.setTitle("Capture new user");
        setSize(600, 150 + (int) Constants.TRAIN_FACE_IMAGE_SIZE.height);
        SpringLayout sl = new SpringLayout();
        setLayout(sl);

        // train button
        trainButton = new JButton(LABEL_TRAIN_BUTTON);
        trainButton.addActionListener(e -> train());
        trainButton.setEnabled(false);
        add(trainButton);
        sl.putConstraint(SpringLayout.WEST, trainButton, 5, SpringLayout.WEST, this.getContentPane());
        sl.putConstraint(SpringLayout.SOUTH, trainButton, -5, SpringLayout.NORTH, statusBar);

        JLabel userLabel = new JLabel("for name: ");
        add(userLabel);
        sl.putConstraint(SpringLayout.WEST, userLabel, 5, SpringLayout.EAST, trainButton);
        sl.putConstraint(SpringLayout.SOUTH, userLabel, -10, SpringLayout.NORTH, statusBar);

        userField = new JTextField();
        add(userField);
        sl.putConstraint(SpringLayout.WEST, userField, 5, SpringLayout.EAST, userLabel);
        sl.putConstraint(SpringLayout.EAST, userField, 200, SpringLayout.EAST, userLabel);
        sl.putConstraint(SpringLayout.SOUTH, userField, -5, SpringLayout.NORTH, statusBar);

        // start button
        startButton = new JButton(LABEL_START_BUTTON);
        startButton.addActionListener(e -> startCapturing());
        add(startButton);
        sl.putConstraint(SpringLayout.WEST, startButton, 5, SpringLayout.WEST, this.getContentPane());
        sl.putConstraint(SpringLayout.NORTH, startButton, 5, SpringLayout.NORTH, this.getContentPane());

        // stop button
        stopButton = new JButton(LABEL_STOP_BUTTON);
        stopButton.addActionListener(e -> {
            stopCapturing();
        });
        stopButton.setEnabled(false);
        add(stopButton);
        sl.putConstraint(SpringLayout.WEST, stopButton, 5, SpringLayout.EAST, startButton);
        sl.putConstraint(SpringLayout.NORTH, stopButton, 5, SpringLayout.NORTH, this.getContentPane());

        // shot button
        JButton shotButton = new JButton(LABEL_SINGLE_SHOT);
        shotButton.addActionListener(e -> takeShot());
        add(shotButton);
        sl.putConstraint(SpringLayout.WEST, shotButton, 5, SpringLayout.EAST, stopButton);
        sl.putConstraint(SpringLayout.NORTH, shotButton, 5, SpringLayout.NORTH, this.getContentPane());

        // close dialog button
        JButton closeButton = new JButton(LABEL_CLOSE_BUTTON);
        closeButton.addActionListener(e -> dispose());
        add(closeButton);
        sl.putConstraint(SpringLayout.EAST, closeButton, -5, SpringLayout.EAST, this.getContentPane());
        sl.putConstraint(SpringLayout.SOUTH, closeButton, -5, SpringLayout.NORTH, statusBar);


        capturedImagesPanel.setLayout(new FlowLayout());

        // the panel for images
        scrollPane = new JScrollPane(capturedImagesPanel);
        add(scrollPane);
        sl.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, startButton);
        sl.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, this.getContentPane());
        sl.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, this.getContentPane());
        sl.putConstraint(SpringLayout.SOUTH, scrollPane, -5, SpringLayout.NORTH, closeButton);

        // sets the status bar
        statusBar.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        add(statusBar);
        sl.putConstraint(SpringLayout.SOUTH, statusBar, -2, SpringLayout.SOUTH, this.getContentPane());
        sl.putConstraint(SpringLayout.WEST, statusBar, 2, SpringLayout.WEST, this.getContentPane());
        sl.putConstraint(SpringLayout.EAST, statusBar, -2, SpringLayout.EAST, this.getContentPane());
    }

    public void startCapturing() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        pictureGrabber = new PictureGrabber();
        pictureGrabber.execute();
    }

    public void stopCapturing() {
        pictureGrabber.cancel(false);
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        startButton.setText(LABEL_RESUME_CAPTURING);
    }

    private void train() {

        if (userField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please specify the username for this set of images.");
            return;
        }

        if (capturedFaces.size() < Constants.MINIMUM_TRAIN_SET_SIZE) {
            JOptionPane.showMessageDialog(this, "You need at least " + Constants.MINIMUM_TRAIN_SET_SIZE);
            return;
        }

        try {
            // saves images to filesystem
            int counter = 0;
            String tempDir = System.getProperty("java.io.tmpdir");
            for (BufferedImage capturedFace : capturedFaces) {
                ImageIO.write(capturedFace, "JPG", new File(tempDir + File.separatorChar + userField.getText() + "_" + (counter++) + ".jpg"));
            }
            JOptionPane.showMessageDialog(this, capturedFaces.size() + " images were saved in " + tempDir);
        } catch (Exception e1) {
            new ErrorDialog(e1).setVisible(true);
        }
    }

    private void takeShot() {
        if (capturedFaces.size() >= Constants.MAX_IMAGES_NUMBER_FOR_TRAINING) {
            return;
        }

        assert(detectorsManager.getDetectors().size() == 1 && detectorsManager.getDetector(Constants.DEFAULT_FACE_CLASSIFIER)!=null);

        ElementsDetector detector = detectorsManager.getDetector(Constants.DEFAULT_FACE_CLASSIFIER);

        // if no face detected from webcam, just skips
        if (detector.getDetectedElements().size() != 1) {
            return;
        }

        Mat face = ImageUtils.resizeFace(detector.getDetectedElements().get(0).getDetectedImageElement());
        image = ImageUtils.getBufferedImageFromMat(face);

        capturedFaces.add(image);
        capturedImagesPanel.add(new JLabel(new ImageIcon(image)));
        UserCaptureDialog.this.statusBar.setText("Captured " + capturedFaces.size() + " images (" + Constants.MINIMUM_TRAIN_SET_SIZE + " are needed for saving)");
        scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMaximum() + Constants.TRAIN_FACE_IMAGE_WIDTH);
        if (capturedFaces.size() >= Constants.MINIMUM_TRAIN_SET_SIZE) {
            trainButton.setEnabled(true);
        }
        capturedImagesPanel.revalidate();
        capturedImagesPanel.repaint();
    }

    private class PictureGrabber extends SwingWorker {

        private final static int PHOTO_DELAY = 1000;

        @Override
        protected Void doInBackground() throws Exception {

            while (!isCancelled()) {
                takeShot();
                Thread.sleep(PHOTO_DELAY);
            }
            return null;
        }

    }
}