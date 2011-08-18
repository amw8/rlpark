package rltoys.algorithms.representations.ltu.units;

import rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class LTUAdaptive extends LTUThreshold {
  private static final long serialVersionUID = 7927007001800685788L;
  static final public double Epsilon = 0.001;
  public final double minFrequency;
  public final double maxFrequency;
  private final double lambda;
  private double averageFrequency;
  private int lastTime;


  public LTUAdaptive(double minFrequency, double maxFrequency) {
    assert minFrequency >= 0.0 && maxFrequency > minFrequency;
    this.minFrequency = minFrequency;
    this.maxFrequency = maxFrequency;
    this.averageFrequency = (minFrequency + maxFrequency) / 2.0;
    lambda = Double.NaN;
  }

  public LTUAdaptive(int index, int[] inputs, byte[] weights, double minFrequency, double maxFrequency) {
    super(index, inputs, weights);
    assert minFrequency >= 0.0 && maxFrequency > minFrequency;
    this.minFrequency = minFrequency;
    this.maxFrequency = maxFrequency;
    this.lambda = Utils.timeStepsToDiscount(((int) (1.0 / minFrequency)) * 10);
  }

  @Override
  public boolean update(int time, double[] inputVector) {
    super.update(time, inputVector);
    assert time > lastTime;
    averageFrequency *= lambda;
    if (isActive)
      averageFrequency += 1 - lambda;
    if (averageFrequency > maxFrequency)
      threshold += Epsilon;
    else if (averageFrequency < minFrequency)
      threshold -= Epsilon;
    lastTime = time;
    return isActive;
  }

  @Override
  public LTUAdaptive newLTU(int index, int[] inputs, byte[] weights) {
    return new LTUAdaptive(index, inputs, weights, minFrequency, maxFrequency);
  }
}
