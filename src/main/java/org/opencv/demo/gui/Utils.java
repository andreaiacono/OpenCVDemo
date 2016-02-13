package org.opencv.demo.gui;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static void centerFrame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation((screenSize.width / 2) - (frameSize.width / 2), (screenSize.height / 2) - (frameSize.height / 2));
    }


    public static Map<String, String[]> loadClassifiers(String dataPath) {

        Map<String, String[]> classifiers = new HashMap<>();
        File dir = new File(dataPath);
        for (String dirName: dir.list()) {
            File file = new File(dataPath + File.separator + dirName);
            if (file.isDirectory()) {
                classifiers.put(file.getName(), file.list());
            }
        }
        return classifiers;
    }

    public static String stackTraceToString(Exception ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pwWriter = new PrintWriter(baos, true);
        ex.printStackTrace(pwWriter);
        return baos.toString();
    }
}
