package rlpark.plugin.opencv.runnables;

import java.awt.image.BufferedImage;

import rlpark.plugin.video.ImageProvider;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class WebCam implements Runnable, ImageProvider {
  private final Clock clock = new Clock();
  private IplImage currentFrame;

  public WebCam() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    try {
      protectedRun();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void protectedRun() throws Exception {
    OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    grabber.start();
    while (clock.tick()) {
      grabber.grab();
    }
    grabber.stop();
  }

  @Override
  public BufferedImage image() {
    return currentFrame != null ? currentFrame.getBufferedImage() : null;
  }
}
