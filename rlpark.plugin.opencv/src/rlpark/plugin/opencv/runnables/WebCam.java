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
    OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    try {
      grabber.start();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    while (clock.tick()) {
      currentFrame = getFrame(grabber);
      if (currentFrame == null)
        return;
    }
    try {
      grabber.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public IplImage getFrame(OpenCVFrameGrabber grabber) {
    try {
      return grabber.grab();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public BufferedImage image() {
    return currentFrame != null ? currentFrame.getBufferedImage() : null;
  }
}
