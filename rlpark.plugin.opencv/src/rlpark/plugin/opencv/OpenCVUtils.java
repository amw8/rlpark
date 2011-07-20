package rlpark.plugin.opencv;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class OpenCVUtils {
  public static void convertScale(IplImage src, IplImage dst) {
    double scale = 0.0;
    if (src.depth() == opencv_core.IPL_DEPTH_32F && dst.depth() == opencv_core.IPL_DEPTH_8U)
      scale = 255;
    if (src.depth() == opencv_core.IPL_DEPTH_8U && dst.depth() == opencv_core.IPL_DEPTH_32F)
      scale = 1. / 255;
    opencv_core.cvConvertScale(src, dst, scale, 0);
  }
}
