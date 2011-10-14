package rlpark.plugin.opencv;

import java.awt.image.BufferedImage;

import rlpark.plugin.opencv.zephyr.OpenCVImageProvider;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.video.ImageProvider;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;

public class FrameGrabber implements ImageProvider {
  private final CvCapture grabber;
  @IgnoreMonitor
  private final OpenCVImageProvider webcam = new OpenCVImageProvider();
  private final ImageBuffer lastImage;
  private final int width;
  private final int height;
  private final int depth;
  private final int channels;

  public FrameGrabber(int deviceNumber) {
    this(deviceNumber, opencv_core.IPL_DEPTH_32F, 1);
  }

  public FrameGrabber(int deviceNumber, int depth, int channels) {
    grabber = opencv_highgui.cvCreateCameraCapture(0);
    IplImage firstImage = grabFirstImage();
    width = firstImage.cvSize().width();
    height = firstImage.cvSize().height();
    lastImage = new ImageBuffer(width, height, depth, channels);
    this.depth = depth;
    this.channels = channels;
  }

  private IplImage grabFirstImage() {
    IplImage firstImage = null;
    int trial = 0;
    while (trial < 50) {
      firstImage = opencv_highgui.cvQueryFrame(grabber);
      if (firstImage != null)
        return firstImage;
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      trial++;
    }
    return firstImage;
  }

  @Override
  public BufferedImage image() {
    return webcam.image();
  }

  public int height() {
    return height;
  }

  public int width() {
    return width;
  }

  public void dispose() {
    try {
      opencv_highgui.cvReleaseCapture(grabber);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public IplImage grab() {
    IplImage result = null;
    try {
      result = opencv_highgui.cvQueryFrame(grabber);
      if (result == null)
        return null;
      lastImage.update(result);
      webcam.update(lastImage);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return lastImage.im();
  }

  public int depth() {
    return depth;
  }

  public int channels() {
    return channels;
  }
}
