package org.opencv.demo.misc;

public enum Classifier {

    HAAR("Haar classifier"),
    LBP("LBP classifier"),
    ANY("No classifier");

    private final String name;

    Classifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
