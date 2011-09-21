package rlpark.plugin.opencv.runnables;

import rlpark.plugin.opencv.MotionMeasure;
import rlpark.plugin.opencv.OpenCVImageProvider;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

@Monitor
public class MotionDetection implements Runnable {
  private final Clock clock = new Clock();
  private final OpenCVImageProvider webcam = new OpenCVImageProvider();
  private final MotionMeasure motionMeasure = new MotionMeasure(.99);

  public MotionDetection() {
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
      IplImage currentFrame = getFrame(grabber);
      if (currentFrame == null)
        return;
      webcam.update(currentFrame);
      motionMeasure.update(currentFrame);
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
}
