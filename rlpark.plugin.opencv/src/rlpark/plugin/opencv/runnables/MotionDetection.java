package rlpark.plugin.opencv.runnables;

import rlpark.plugin.opencv.FrameGrabber;
import rlpark.plugin.opencv.MotionMeasure;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

@Monitor
public class MotionDetection implements Runnable {
  private final Clock clock = new Clock();
  private final FrameGrabber grabber = new FrameGrabber(0);
  private final MotionMeasure motionMeasure;

  public MotionDetection() {
    motionMeasure = new MotionMeasure(grabber.width(), grabber.height(), .99);
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (clock.tick()) {
      IplImage currentFrame = grabber.grab();
      if (currentFrame == null)
        return;
      motionMeasure.update(currentFrame);
    }
    grabber.dispose();
  }
}
