package org.opencv.demo.misc;

import org.opencv.demo.gui.swing.OpenCvDemo;

public class SwingLogger implements Loggable {

    private OpenCvDemo openCvDemo;

    public SwingLogger(OpenCvDemo openCvDemo) {
        this.openCvDemo = openCvDemo;
    }

    public void log(String message) {
        openCvDemo.consoleLog(message);
    }
}
