package rlpark.plugin.rltoysview.views.nostate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import rlpark.plugin.rltoysview.internal.policystructure.NormalDistributionDrawer;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistribution;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.TRStep;
import rltoys.experiments.continuousaction.NoStateExperiment;
import rltoys.math.History;
import rltoys.math.normalization.MinMaxNormalizer;
import rltoys.math.ranges.Range;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.plot2d.Data2D;
import zephyr.plugin.plotting.plot2d.Plot2D;
import zephyr.plugin.plotting.plot2d.drawer2d.Drawer2D;

public class NoStateView extends ForegroundCanvasView<NoStateExperiment> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(NoStateExperiment.class);
    }
  }

  protected class ExperimentData implements Listener<Clock> {
    static final public int HistoryLength = 1000;
    public final History actionHistory = new History(HistoryLength);
    public final History rewardHistory = new History(HistoryLength);

    @Override
    public void listen(Clock eventInfo) {
      if (experiment == null)
        return;
      TRStep step = experiment.step();
      final double action = ((ActionArray) step.a_t).actions[0];
      actionHistory.append(action);
      rewardHistory.append(step.r_tp1);
    }

    public void reset() {
      actionHistory.reset();
      rewardHistory.reset();
    }

    public int nbAdded() {
      return actionHistory.nbAdded();
    }
  }

  final ExperimentData experimentData = new ExperimentData();
  NoStateExperiment experiment = null;
  private final Plot2D plot = new Plot2D();
  private final Drawer2D rewardDrawer = new Drawer2D() {
    @Override
    public void draw(GC gc, float[] xdata, float[] ydata, int[] gx, int[] gy) {
      for (int i = 1; i <= Math.min(gy.length, experimentData.nbAdded()); i++)
        gc.drawOval(gx[gx.length - i] - radius, gy[gy.length - i] - radius, radius, radius);
    }
  };
  private NormalDistributionDrawer normalDistributionDrawer;
  private Data2D data;
  private final Colors colors = new Colors();
  private final MinMaxNormalizer rewardNormalizer = new MinMaxNormalizer(new Range(0, 1));
  int radius = 1;

  @Override
  public boolean synchronize() {
    experimentData.actionHistory.toArray(data.xdata);
    experimentData.rewardHistory.toArray(data.ydata);
    for (float reward : data.ydata)
      rewardNormalizer.update(reward);
    for (int i = 0; i < data.ydata.length; i++)
      data.ydata[i] = rewardNormalizer.normalize(data.ydata[i]);
    normalDistributionDrawer.synchronize();
    return true;
  }

  @Override
  protected void paint(GC gc) {
    plot.clear(gc);
    gc.setAntialias(ZephyrPlotting.preferredAntiAliasing() ? SWT.ON : SWT.OFF);
    if (experiment == null)
      return;
    gc.setForeground(colors.color(gc, Colors.COLOR_DARK_RED));
    radius = ZephyrPlotting.preferredLineSize();
    gc.setLineWidth(ZephyrPlotting.preferredLineSize());
    plot.draw(gc, rewardDrawer, data);
    gc.setForeground(colors.color(gc, Colors.COLOR_BLACK));
    gc.setLineWidth(ZephyrPlotting.preferredLineSize());
    normalDistributionDrawer.draw(gc);
  }

  @Override
  public void dispose() {
    super.dispose();
    colors.dispose();
  }

  @Override
  public void unset() {
    experiment = null;
  }

  @Override
  protected void set(NoStateExperiment current) {
    experiment = current;
    experimentData.reset();
    rewardNormalizer.reset();
    data = new Data2D("Reward", experimentData.actionHistory.length);
    NormalDistribution policy = (NormalDistribution) ((ActorCritic) experiment.control).actors[0].policy();
    normalDistributionDrawer = new NormalDistributionDrawer(plot, policy, rewardNormalizer.newInstance());
    setViewName(policy.getClass().getSimpleName(), "");
    instance.clock().onTick.connect(experimentData);
  }

  @Override
  protected Class<?> classSupported() {
    return NoStateExperiment.class;
  }
}
