package rlpark.plugin.videoview.internal;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import rlpark.plugin.video.ImageListener;
import rlpark.plugin.video.VideoPlayer;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.ProvidedView;

public class VideoView extends ViewPart implements ImageListener, ProvidedView {
  static public class VideoViewProvider extends ClassViewProvider {
    public VideoViewProvider() {
      super(VideoPlayer.class);
    }
  }

  private final Runnable drawer = new Runnable() {
    @Override
    public void run() {
      displayImage();
    }
  };
  private ImageData imageData;
  Image image;
  private Composite parent;

  public VideoView() {
  }

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    parent.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        if (image != null)
          e.gc.drawImage(image, 0, 0);
      }
    });
  }

  @Override
  public void setFocus() {
  }

  protected void displayImage() {
    if (image != null)
      image.dispose();
    if (imageData == null)
      return;
    if (parent.isDisposed())
      return;
    image = new Image(parent.getDisplay(), imageData);
    parent.redraw();
    parent.update();
  }

  static ImageData convertToSWT(BufferedImage bufferedImage) {
    ColorModel colorModel = bufferedImage.getColorModel();
    if (colorModel instanceof DirectColorModel) {
      DirectColorModel directColorModel = (DirectColorModel) colorModel;
      PaletteData palette = new PaletteData(directColorModel.getRedMask(), directColorModel.getGreenMask(),
                                            directColorModel.getBlueMask());
      ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
                                     directColorModel.getPixelSize(), palette);
      for (int y = 0; y < data.height; y++) {
        for (int x = 0; x < data.width; x++) {
          int rgb = bufferedImage.getRGB(x, y);
          int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
          data.setPixel(x, y, pixel);
          if (directColorModel.hasAlpha()) {
            data.setAlpha(x, y, (rgb >> 24) & 0xFF);
          }
        }
      }
      return data;
    } else if (colorModel instanceof IndexColorModel) {
      IndexColorModel indexColorModel = (IndexColorModel) colorModel;
      int size = indexColorModel.getMapSize();
      byte[] reds = new byte[size];
      byte[] greens = new byte[size];
      byte[] blues = new byte[size];
      indexColorModel.getReds(reds);
      indexColorModel.getGreens(greens);
      indexColorModel.getBlues(blues);
      RGB[] rgbs = new RGB[size];
      for (int i = 0; i < rgbs.length; i++) {
        rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
      }
      PaletteData palette = new PaletteData(rgbs);
      ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
                                     indexColorModel.getPixelSize(),
                                     palette);
      data.transparentPixel = indexColorModel.getTransparentPixel();
      WritableRaster raster = bufferedImage.getRaster();
      int[] pixelArray = new int[1];
      for (int y = 0; y < data.height; y++) {
        for (int x = 0; x < data.width; x++) {
          raster.getPixel(x, y, pixelArray);
          data.setPixel(x, y, pixelArray[0]);
        }
      }
      return data;
    } else if (colorModel instanceof ComponentColorModel) {
      ComponentColorModel componentColorModel = (ComponentColorModel) bufferedImage.getColorModel();
      PaletteData palette = new PaletteData(0x0000FF, 0x00FF00, 0xFF0000);
      ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
                                     componentColorModel.getPixelSize(), palette);
      // This is valid because we are using a 3-byte Data model with no
      // transparent pixels
      data.transparentPixel = -1;
      WritableRaster raster = bufferedImage.getRaster();
      int[] pixelArray = new int[3];
      for (int y = 0; y < data.height; y++) {
        for (int x = 0; x < data.width; x++) {
          raster.getPixel(x, y, pixelArray);
          int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
          data.setPixel(x, y, pixel);
        }
      }
      return data;
    }
    return null;
  }

  @Override
  public void setImage(BufferedImage bufferedImage) {
    if (parent.isDisposed())
      return;
    imageData = convertToSWT(bufferedImage);
    Display display = parent.getDisplay();
    if (display != null)
      display.syncExec(drawer);
  }

  @Override
  public boolean synchronize(Clock clock) {
    return false;
  }

  @Override
  public void repaint() {
  }

  @Override
  public boolean[] provide(CodeNode[] codeNode) {
    Object drawn = ((ClassNode) codeNode[0]).instance();
    ((VideoPlayer) drawn).setImageLister(this);
    return new boolean[] { true };
  }

  @Override
  public void dispose() {
    if (image != null)
      image.dispose();
  }
}
