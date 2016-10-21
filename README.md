# OpenCVDemo
This is a toy project for testing OpenCV classifiers and face recognition.
It works with OpenCV 3.1 and Java and looks like this:
<br/>
![OpenCV demo screenshot](https://raw.githubusercontent.com/andreaiacono/andreaiacono.github.io/master/img/opencv_demo.png)

## Project
The application has a Swing GUI and uses OpenCV for capturing the webcam stream; the face detection/recognition is completely decoupled from the GUI because I need to use these classes for a GUIless application on a raspberry.

### Recognition
To test face recognition, you have to choose menu _Face Recognition -> Capture new user_ and take at least 20 photos; then you have to press the "Save images" button adding a USERNAME in the input text and the application will save the images to file system (in the <code>java.io.tmpdir</code> dir where the filename has the format USERNAME_N.jpg); now you have to close the application, copy the images to the <code>/src/main/resources/faces</code> directory and restart the application (sooner or later I'll fix this behaviuor). 

Now you can choose _Face Recognition -> Start recognition_ to see face recognition in action.

OpenCV supports three different recognizers that you can choose from the menu _Face Recognition -> recognizers_.

### Standalone classes
In package [org/opencv/demo/gui/swing/standalone](https://github.com/andreaiacono/OpenCVDemo/tree/master/src/main/java/org/opencv/demo/gui/swing/standalone) there are a couple of classes that can be used as standalone classes to show how works face detection and recognition.

## Installation
For installing OpenCV with Java on Ubuntu, I've followed this tutorial: http://milq.github.io/install-opencv-ubuntu-debian/ ; if you're using Mac or Windows, I'm sure it's plenty of tutorial on how to do that.
Please note that when you have to download the archive from github repository, you first have to choose the <code>3.1.0</code> tag (the download link is [https://github.com/Itseez/opencv/archive/3.1.0.zip](https://github.com/Itseez/opencv/archive/3.1.0.zip)).

### Enabling Face Recognition when compiling OpenCV
To enable Face Recognition, you need to:
* download the 3.1 version of the opencv_contrib: https://github.com/Itseez/opencv_contrib/archive/3.1.0.zip
* extract the archive
* open file opencv_contrib-3.1.0/modules/face/CMakeLists.txt
* append "java" to the WRAP section, so that it reads:
  <code>ocv_define_module(face opencv_core opencv_imgproc opencv_objdetect WRAP python java)</code>
* add the switch <code>-D OPENCV_EXTRA_MODULES_PATH=$YOUR_OPENCV_CONTRIB_PATH/opencv_contrib-3.1.0/modules </code> to the compile command showed in the installation tutorial linked above
* once compiled, you'll find the <code>opencv-310.jar</code> in the <code>build/bin</code> directory: set that JAR as a library for the Java project 
