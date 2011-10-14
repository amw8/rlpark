package rlpark.plugin.opencv;

import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

@Monitor
public class MotionMeasure {
  private final ImageTrace trace;
  private final ImageBuffer difference;
  @IgnoreMonitor
  private final double area;
  private double measure;

  public MotionMeasure(double lambda, int width, int height, int depth, int channels) {
    trace = new ImageTrace(lambda, width, height, depth, channels);
    difference = new ImageBuffer(width, height, depth, channels);
    area = width * height;
  }

  public double update(IplImage currentFrame) {
    trace.update(currentFrame);
    opencv_core.cvAbsDiff(trace.im(), currentFrame, difference.im());
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

  public int channels() {
    return trace.im().nChannels();
  }

  public ImageTrace trace() {
    return trace;
  }
}
