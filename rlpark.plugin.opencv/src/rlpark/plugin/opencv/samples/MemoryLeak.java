package rlpark.plugin.opencv.samples;

import com.googlecode.javacv.OpenCVFrameGrabber;

public class MemoryLeak implements Runnable {
  public MemoryLeak() throws Exception {
  }

  private void protectedRun() throws Exception {
    OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    grabber.start();
    while (true) {
      grabber.grab();
    }
  }

  @Override
  public void run() {
    try {
      protectedRun();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    Thread thread = new Thread(new MemoryLeak());
    thread.start();
    while (true) {
      Thread.sleep(10000);
    }
  }
}
