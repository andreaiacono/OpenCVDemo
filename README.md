# OpenCVDemo
This is a toy project for testing opencv classifiers and face recognition.
It works with OpenCV 3.1 and Java and looks like this:
<br/>
![OpenCV demo screenshot](https://raw.githubusercontent.com/andreaiacono/andreaiacono.github.io/master/img/opencv_demo.png)

## Project
The applicatin has a Swing GUI and uses OpenCV for capturing from the webcam; the face detection/recognition is completely decolupled from the GUI because I need to use these classes for a GUIless application on a raspberry.
In package org/opencv/demo/gui/swing/standalone there are a couple of classes that can be used a standalone classes to show how works face detection and recognition.

## Installation
For installing OpenCV with Java on Ubuntu, I've followed this tutorial: http://milq.github.io/install-opencv-ubuntu-debian/ ; if you're using Mac or Windows, I'm sure it's plenty of tutorial on how to do that.
Please note that when you have to download the archive from github repository, you first have to choose the <code>3.1.0</code> tag!

### Enabling Face Recognition
For enabling Face Recognition, you need to:
* download the 3.1 version of the opencv_contrib: https://github.com/Itseez/opencv_contrib/archive/3.1.0.zip
* extract the archive
* open file opencv_contrib-3.1.0/modules/face/CMakeLists.txt
* append "java" to the WRAP section, so that it reads:
  <code>ocv_define_module(face opencv_core opencv_imgproc opencv_objdetect WRAP python java)</code>
* add the switch <code>-D OPENCV_EXTRA_MODULES_PATH=$YOUR_OPENCV_CONTRIB_PATH/opencv_contrib-3.1.0/modules </code> to the compile command showed in the installation tutorial linked above
