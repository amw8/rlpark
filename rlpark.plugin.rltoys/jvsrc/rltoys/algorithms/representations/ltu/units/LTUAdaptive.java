package rltoys.algorithms.representations.ltu.units;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class LTUAdaptive extends LTUThreshold {
  private static final long serialVersionUID = 7927007001800685788L;
  static final public double Lambda = .99;
  static final public double Epsilon = 0.001;
  private double averageFrequency = 0.0;
  public final double minFrequency;
  public final double maxFrequency;

  public LTUAdaptive(double minFrequency, double maxFrequency) {
    assert minFrequency >= 0.0 && maxFrequency > minFrequency;
    this.minFrequency = minFrequency;
    this.maxFrequency = maxFrequency;
  }

  public LTUAdaptive(int index, int[] inputs, byte[] weights, double minFrequency, double maxFrequency) {
    super(index, inputs, weights);
    assert minFrequency >= 0.0 && maxFrequency > minFrequency;
    this.minFrequency = minFrequency;
    this.maxFrequency = maxFrequency;
  }

  @Override
  public void update() {
    super.update();
    averageFrequency = Lambda * averageFrequency + (1 - Lambda) * (isActive() ? 1.0 : 0.0);
    if (averageFrequency > maxFrequency)
      threshold += Epsilon;
    if (averageFrequency < minFrequency)
      threshold -= Epsilon;
  }

  @Override
  public LTUAdaptive newLTU(int index, int[] inputs, byte[] weights) {
    return new LTUAdaptive(index, inputs, weights, minFrequency, maxFrequency);
  }
}
