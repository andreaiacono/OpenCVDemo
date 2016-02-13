package org.opencv.demo.gui.swing;

import org.opencv.demo.gui.Utils;

import javax.swing.*;
import java.awt.*;

public class ErrorDialog extends JDialog {

    private JTextArea errorMessageText;
    private JButton closeButton, detailsButton;
    private JLabel messageLabel;
    private JScrollPane scrollPane;
    private String message, stackTrace;

    public ErrorDialog(Exception ex) {
        this(ex.getMessage(), Utils.stackTraceToString(ex));
    }

    public ErrorDialog(String message, String stackTrace) {
        super();
        setWindowTitle("Error Message");
        setModal(true);
        setSize(600, 100);
        setResizable(true);
        this.message = message;
        this.stackTrace = stackTrace;
        init();
    }

    private void init() {
        if (message == null) message = "NullPointerException";
        messageLabel = new JLabel("An error has occurred: " + message, JLabel.CENTER);
        messageLabel.setIconTextGap(10);
        Icon icon = (Icon) UIManager.getLookAndFeel().getDefaults().get("OptionPane.errorIcon");
        messageLabel.setIcon(icon);
        messageLabel.setPreferredSize(new Dimension(100, 40));

        errorMessageText = new JTextArea(10, 50);
        scrollPane = new JScrollPane(errorMessageText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        errorMessageText.setEditable(false);
        scrollPane.setVisible(false);

        // prints the error on the textarea
        errorMessageText.append("\n\nERROR: " + message);
        errorMessageText.append("\n\n" + stackTrace);
        errorMessageText.setCaretPosition(0);

        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> this.dispose());

        detailsButton = new JButton("Show Details");
        detailsButton.addActionListener(e -> updateDetails());

        SpringLayout sl = new SpringLayout();
        setLayout(sl);

        sl.putConstraint(SpringLayout.WEST, messageLabel, 10, SpringLayout.WEST, this.getContentPane());
        sl.putConstraint(SpringLayout.NORTH, messageLabel, 5, SpringLayout.NORTH, this.getContentPane());
        sl.putConstraint(SpringLayout.EAST, messageLabel, -5, SpringLayout.EAST, this.getContentPane());

        sl.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, this.getContentPane());
        sl.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, messageLabel);
        sl.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, this.getContentPane());
        sl.putConstraint(SpringLayout.SOUTH, scrollPane, -5, SpringLayout.NORTH, closeButton);

        sl.putConstraint(SpringLayout.EAST, closeButton, -5, SpringLayout.EAST, this.getContentPane());
        sl.putConstraint(SpringLayout.SOUTH, closeButton, -5, SpringLayout.SOUTH, this.getContentPane());

        sl.putConstraint(SpringLayout.WEST, detailsButton, 5, SpringLayout.WEST, this.getContentPane());
        sl.putConstraint(SpringLayout.SOUTH, detailsButton, -5, SpringLayout.SOUTH, this.getContentPane());

        add(scrollPane);
        add(closeButton);
        add(detailsButton);
        add(messageLabel);

    }

    protected void setWindowTitle(String strTitle) {
        super.setTitle(strTitle);
    }

    private void updateDetails() {

        if (scrollPane.isVisible()) {
            this.setSize(650, 100);
            scrollPane.setVisible(false);
            detailsButton.setText("Show Details");
        }
        else {
            this.setSize(650, 500);
            scrollPane.setVisible(true);
            detailsButton.setText("Hide Details");
        }

        repaint();
    }
}