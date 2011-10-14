package rlpark.plugin.opencv.runnables;

import rlpark.plugin.opencv.FrameGrabber;
import rlpark.plugin.opencv.ImageTrace;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

@Monitor
public class TraceRunnable implements Runnable {
  private final Clock clock = new Clock();
  private final FrameGrabber webcam = new FrameGrabber(0);
  private final ImageTrace trace = new ImageTrace(0.9, webcam.width(), webcam.height(), webcam.depth(),
                                                  webcam.channels());

  public TraceRunnable() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (clock.tick()) {
      IplImage image = webcam.grab();
      trace.update(image);
    }
  }
}
