package rlpark.plugin.opencv;

import java.awt.image.BufferedImage;

import rlpark.plugin.opencv.zephyr.OpenCVImageProvider;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.video.ImageProvider;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FrameGrabber implements ImageProvider {
  private final OpenCVFrameGrabber grabber;
  @IgnoreMonitor
  private final OpenCVImageProvider webcam = new OpenCVImageProvider();
  private final int width;
  private final int height;
  private final int depth;
  private final int channels;

  public FrameGrabber(int deviceNumber) {
    grabber = new OpenCVFrameGrabber(0);
    try {
      grabber.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
    IplImage firstImage = grab();
    width = firstImage.cvSize().width();
    height = firstImage.cvSize().height();
    depth = firstImage.depth();
    channels = firstImage.nChannels();
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
      grabber.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public IplImage grab() {
    IplImage result = null;
    try {
      result = grabber.grab();
      if (result == null)
        return null;
      webcam.update(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public int depth() {
    return depth;
  }

  public int channels() {
    return channels;
  }
}
