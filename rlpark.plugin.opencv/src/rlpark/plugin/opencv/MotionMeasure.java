package rlpark.plugin.opencv;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

@Monitor
public class MotionMeasure {
  private static final int DEPTH = opencv_core.IPL_DEPTH_32F;
  private final OpenCVImageBuffer buffer = new OpenCVImageBuffer(DEPTH, 1);
  private final OpenCVImageBuffer current = new OpenCVImageBuffer(DEPTH, 1);
  private final OpenCVImageBuffer background = new OpenCVImageBuffer(DEPTH, 1);
  private final OpenCVImageBuffer difference = new OpenCVImageBuffer(DEPTH, 1);
  private double area;
  private final double lambda = 0.999;
  private double measure;

  public double update(IplImage currentFrame) {
    current.update(currentFrame);
    if (background.im() == null) {
      background.update(currentFrame);
      buffer.update(currentFrame);
      difference.update(currentFrame);
      area = currentFrame.height() * currentFrame.width();
    }
    opencv_core.cvScale(background.im(), background.im(), lambda, 0.0);
    opencv_core.cvScale(current.im(), buffer.im(), 1.0 - lambda, 0.0);
    opencv_core.cvAdd(background.im(), buffer.im(), background.im(), null);
    opencv_core.cvAbsDiff(background.im(), current.im(), difference.im());

    measure = toValue(opencv_core.cvSum(difference.im()));
    return measure;
  }

  private double toValue(CvScalar scalar) {
    return (scalar.red() + scalar.green() + scalar.blue()) / area;
  }

  public void dispose() {
    buffer.dispose();
    current.dispose();
    background.dispose();
    difference.dispose();
  }
}
