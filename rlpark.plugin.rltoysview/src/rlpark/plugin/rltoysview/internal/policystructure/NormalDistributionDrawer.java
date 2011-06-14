package rlpark.plugin.rltoysview.internal.policystructure;

import org.eclipse.swt.graphics.GC;

import rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistribution;
import rltoys.math.normalization.MinMaxNormalizer;
import rltoys.utils.Utils;
import zephyr.plugin.plotting.plot2d.Data2D;
import zephyr.plugin.plotting.plot2d.Plot2D;

public class NormalDistributionDrawer {
  static private final int NbDrawnPoints = 100;
  static private final double Width = 10;

  private final Data2D datas = new Data2D("", NbDrawnPoints);
  private final NormalDistribution policy;
  private final Plot2D plot;
  private double stddev;
  private double mean;
  private final MinMaxNormalizer normalizer;

  public NormalDistributionDrawer(Plot2D plot, NormalDistribution policy) {
    this(plot, policy, null);
  }

  public NormalDistributionDrawer(Plot2D plot, NormalDistribution policy, MinMaxNormalizer normalizer) {
    this.policy = policy;
    this.plot = plot;
    this.normalizer = normalizer;
  }

  public void draw(GC gc) {
    if (!Utils.checkValue((float) (stddev * stddev)) || !Utils.checkValue(mean))
      return;
    plot.draw(gc, datas);
  }

  public void synchronize() {
    stddev = policy.stddev();
    mean = policy.mean();
    if (!Utils.checkValue((float) (stddev * stddev)) || !Utils.checkValue(mean))
      return;
    double range = (float) (stddev * Width);
    double step = range / datas.nbPoints;
    double minSample = (float) (mean - (range / 2));
    for (int i = 0; i < datas.nbPoints; i++) {
      double a = minSample + step * i;
      datas.xdata[i] = (float) a;
      datas.ydata[i] = (float) policy.pi_s(a);
      if (normalizer != null)
        normalizer.update(datas.ydata[i]);
    }
    if (normalizer != null)
      for (int i = 0; i < datas.nbPoints; i++)
        datas.ydata[i] = normalizer.normalize(datas.ydata[i]);
  }
}
