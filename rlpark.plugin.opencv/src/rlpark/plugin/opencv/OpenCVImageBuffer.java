package rlpark.plugin.opencv;

import java.awt.image.BufferedImage;

import zephyr.plugin.core.api.video.ImageProvider;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc;

public class OpenCVImageBuffer implements ImageProvider {
  private final OpenCVImageProvider imageProvider = new OpenCVImageProvider();
  private final int depth;
  private final int channels;
  private IplImage image;
  private IplImage channelBuffer;

  public OpenCVImageBuffer(int depth, int channels) {
    this.depth = depth;
    this.channels = channels;
  }

  @Override
  public BufferedImage image() {
    imageProvider.update(image);
    return imageProvider.image();
  }

  public void update(IplImage frame) {
    if (image == null)
      image = IplImage.create(frame.width(), frame.height(), depth, channels);
    IplImage channelConverted = convertChannelIFN(frame);
    if (channelConverted.depth() == image.depth())
      opencv_core.cvCopy(channelConverted, image);
    else
      OpenCVUtils.convertScale(channelConverted, image);
  }

  private IplImage convertChannelIFN(IplImage current) {
    if (current.nChannels() == channels)
      return current;
    if (channelBuffer == null)
      channelBuffer = IplImage.create(current.width(), current.height(), current.depth(), channels);
    opencv_imgproc.cvCvtColor(current, channelBuffer, opencv_imgproc.CV_RGB2GRAY);
    return channelBuffer;
  }

  public IplImage im() {
    return image;
  }

  public void dispose() {
    image.release();
    imageProvider.dispose();
    if (channelBuffer != null)
      channelBuffer.release();
  }
}
