package rlpark.plugin.opencv.runnables;

import rlpark.plugin.opencv.FrameGrabber;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

public class WebcamRunnable implements Runnable {
  private final Clock clock = new Clock();
  private final FrameGrabber webcam = new FrameGrabber(0);

  public WebcamRunnable() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (clock.tick()) {
      webcam.grab();
    }
  }
}
