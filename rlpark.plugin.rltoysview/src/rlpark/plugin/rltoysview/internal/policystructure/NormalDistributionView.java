package rlpark.plugin.rltoysview.internal.policystructure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistribution;
import rltoys.algorithms.learning.predictions.td.TD;
import rltoys.math.History;
import rltoys.math.normalization.MinMaxNormalizer;
import rltoys.math.ranges.Range;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.plotting.plot2d.Data2D;
import zephyr.plugin.plotting.plot2d.Plot2DView;
import zephyr.plugin.plotting.plot2d.drawer2d.Drawers;

public class NormalDistributionView extends Plot2DView<NormalDistribution> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(NormalDistribution.class);
    }
  }

  public static final int HistoryLength = 1000;
  private final static String ActionFlagKey = "ActionFlagKey";

  private NormalDistributionDrawer initialNormalDistributionDrawer = null;
  private NormalDistributionDrawer normalDistributionDrawer = null;
  private NormalDistribution normalDistribution = null;
  private MinMaxNormalizer tdErrorNormalized = null;
  private ActorCritic actorCritic = null;
  private final Listener<Clock> clockListener = new Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      updateData();
    }
  };
  protected boolean displayActionFlag;
  private final History actionHistory = new History(HistoryLength);
  private final History tdErrorHistory = new History(HistoryLength);
  private final Data2D data = new Data2D(HistoryLength);

  protected void updateData() {
    if (normalDistribution == null)
      return;
    actionHistory.append(normalDistribution.a_t);
    if (actorCritic != null) {
      double delta_t = ((TD) actorCritic.critic).delta_t;
      tdErrorNormalized.update(delta_t);
      tdErrorNormalized.update(-delta_t);
      tdErrorHistory.append(delta_t);
    }
  }

  @Override
  protected void setSettingBar(Composite settingBar) {
    Button displayAction = new Button(settingBar, SWT.CHECK);
    displayAction.setText("Actions");
    displayAction.setSelection(displayActionFlag);
    displayAction.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        displayActionFlag = !displayActionFlag;
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        displayActionFlag = !displayActionFlag;
      }
    });
    super.setSettingBar(settingBar);
  }

  @Override
  public boolean synchronize() {
    if (initialNormalDistributionDrawer == null) {
      initialNormalDistributionDrawer = new NormalDistributionDrawer(plot, normalDistribution);
      initialNormalDistributionDrawer.synchronize();
    }
    if (plot.axes().y.transformationValid) {
      actionHistory.toArray(data.xdata);
      tdErrorHistory.toArray(data.ydata);
      float scale = plot.axes().y.max();
      for (int i = 0; i < data.ydata.length; i++)
        data.ydata[i] = tdErrorNormalized.normalize(data.ydata[i]) * scale;
    }
    normalDistributionDrawer.synchronize();
    return true;
  }

  @Override
  protected void set(NormalDistribution current) {
    CodeNode codeNode = instance.codeNode();
    normalDistribution = instance.current();
    normalDistributionDrawer = new NormalDistributionDrawer(plot, normalDistribution);
    tdErrorNormalized = new MinMaxNormalizer(new Range(0, 1));
    ClassNode actorCriticParentNode = CodeTrees.findParent(codeNode, ActorCritic.class);
    actorCritic = actorCriticParentNode != null ? (ActorCritic) actorCriticParentNode.instance() : null;
    setViewName(String.format("%s[%s]", normalDistribution.getClass().getSimpleName(), codeNode.label()), "");
    CodeTrees.clockOf(codeNode).onTick.connect(clockListener);
  }

  @Override
  public void paint(PainterMonitor painterListener, Image image, GC gc) {
    if (normalDistributionDrawer == null)
      return;
    plot.clear(gc);
    gc.setAntialias(ZephyrPlotting.preferredAntiAliasing() ? SWT.ON : SWT.OFF);
    gc.setLineWidth(ZephyrPlotting.preferredLineSize());
    gc.setForeground(plot.colors.color(gc, Colors.COLOR_GRAY));
    if (initialNormalDistributionDrawer != null)
      initialNormalDistributionDrawer.draw(gc);
    gc.setForeground(plot.colors.color(gc, Colors.COLOR_BLACK));
    normalDistributionDrawer.draw(gc);
    if (displayActionFlag && plot.axes().y.transformationValid) {
      gc.setForeground(plot.colors.color(gc, Colors.COLOR_DARK_BLUE));
      plot.draw(gc, Drawers.Dots, data);
    }
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    if (memento == null)
      return;
    Boolean savedActionFlag = memento.getBoolean(ActionFlagKey);
    displayActionFlag = savedActionFlag != null ? savedActionFlag : false;
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    memento.putBoolean(ActionFlagKey, displayActionFlag);
  }

  @Override
  public void unset() {
    instance.clock().onTick.disconnect(clockListener);
    initialNormalDistributionDrawer = null;
    normalDistributionDrawer = null;
    normalDistribution = null;
    tdErrorNormalized = null;
    actorCritic = null;
    actionHistory.reset();
    tdErrorHistory.reset();
  }

  @Override
  protected Class<?> classSupported() {
    return NormalDistribution.class;
  }
}
