package rlpark.plugin.opencv;

import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

@Monitor
public class MotionMeasure {
  @IgnoreMonitor
  private static final int DEPTH = opencv_core.IPL_DEPTH_32F;
  private final ImageTrace trace;
  private final ImageBuffer current;
  private final ImageBuffer difference;
  private final double area;
  private double measure;

  public MotionMeasure(int width, int height, double lambda) {
    trace = new ImageTrace(lambda, width, height, DEPTH, 1);
    current = new ImageBuffer(width, height, DEPTH, 1);
    difference = new ImageBuffer(width, height, DEPTH, 1);
    area = width * height;
  }

  public double update(IplImage currentFrame) {
    current.update(currentFrame);
    trace.update(current.im());
    opencv_core.cvAbsDiff(trace.im(), current.im(), difference.im());
    measure = toValue(opencv_core.cvSum(difference.im()));
    return measure;
  }

  private double toValue(CvScalar scalar) {
    return (scalar.red() + scalar.green() + scalar.blue()) / area;
  }

  public void dispose() {
    trace.dispose();
    difference.dispose();
  }
}
