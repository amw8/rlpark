package rlpark.plugin.rltoysview.internal.vectors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import rltoys.math.vector.RealVector;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.helpers.BackgroundCanvasView;
import zephyr.plugin.plotting.actions.CenterPlotAction;
import zephyr.plugin.plotting.actions.CenterPlotAction.ViewCenterable;
import zephyr.plugin.plotting.bar2d.Bar2D;
import zephyr.plugin.plotting.mousesearch.MouseSearch;

public class RealVectorBarView extends BackgroundCanvasView<RealVector> implements ViewCenterable {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(RealVector.class);
    }
  }

  private RealVector copy;
  private final Bar2D bar = new Bar2D();
  private float[] data;
  private MouseSearch mouseSearch;

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    mouseSearch = new MouseSearch(bar, backgroundCanvas.canvas());
    backgroundCanvas.addOverlay(mouseSearch);
  }

  @Override
  protected void setToolbar(IToolBarManager toolBarManager) {
    toolBarManager.add(new CenterPlotAction(this));
  }

  @Override
  public boolean synchronize() {
    copy = instance.current().copy();
    return true;
  }

  @Override
  public void paint(PainterMonitor painterListener, Image image, GC gc) {
    gc.setAntialias(SWT.OFF);
    bar.clear(gc);
    if (data == null)
      return;
    copy.accessData(data);
    bar.draw(gc, data);
  }

  @Override
  public void set(RealVector instance) {
    setViewName();
    data = new float[instance.getDimension()];
  }

  @Override
  public void unset() {
    copy = null;
    data = null;
  }

  @Override
  public void dispose() {
    super.dispose();
    bar.dispose();
  }

  @Override
  protected Class<?> classSupported() {
    return RealVector.class;
  }

  @Override
  public void center() {
    bar.axes().x.reset();
    bar.axes().y.reset();
  }
}
