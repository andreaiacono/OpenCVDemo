package org.opencv.demo.gui.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.opencv.demo.gui.swing.WebcamPanel;
import org.opencv.demo.misc.Constants;
import org.opencv.demo.misc.EmptyLogger;
import org.opencv.demo.core.DetectorsManager;
import org.opencv.core.Core;

import javax.swing.*;


public class OpenCvDemoFxGui extends Application {

    private WebcamPanel webcamPanel;

    private JTextArea consoleOutputTextArea;
    private JLabel statusBar;

    @Override
    public void start(Stage stage) throws Exception{
        consoleOutputTextArea = new JTextArea("Started " + Constants.OPENCVDEMO_COMPLETE);

        // the main window top pane
        webcamPanel = new WebcamPanel(new EmptyLogger(), new DetectorsManager(new EmptyLogger()));
        JScrollPane webcamScrollPane = new JScrollPane(webcamPanel);
        webcamScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // the main window bottom pane

        consoleOutputTextArea.setEditable(false);
        JScrollPane consoleScrollPane = new JScrollPane(consoleOutputTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        consoleScrollPane.setWheelScrollingEnabled(true);

        Parent root = FXMLLoader.load(getClass().getResource("/fx/OpenCvDemoGui.fxml"));
        Scene scene = new Scene(root, 560, 240);
        scene.setFill(Color.GHOSTWHITE);
        stage.setTitle(Constants.OPENCVDEMO_COMPLETE);
        stage.setScene(scene);

        webcamPanel.startCamera();
        stage.show();
    }

    public void consoleLog(String message) {
        consoleOutputTextArea.append("\n" + message);
        consoleOutputTextArea.repaint();
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
