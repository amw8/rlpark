package rltoys.algorithms.representations.ltu;

import java.util.Random;

import rltoys.math.vector.BinaryVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class LTUAdaptive extends LTUThreshold implements LTUAdaptiveDensity {
  private static final long serialVersionUID = 7927007001800685788L;
  static final public double Lambda = .99;
  static final public double Epsilon = 0.001;
  private double averageFrequency = 0.0;
  public final double minFrequency;
  public final double maxFrequency;
  private int[] inputs;

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
    this.inputs = inputs.clone();
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
  public void decreaseDensity(Random random, BinaryVector obs) {
    int bit = chooseBit(random);
    byte weight = inputsToWeights.get(bit);
    boolean inputActive = obs.getEntry(bit) > 0;
    if ((weight == 1 && inputActive) || (weight == -1 && !inputActive)) {
      inputsToWeights.put(bit, (byte) (weight * -1));
      threshold += 1;
    }
  }

  @Override
  public void increaseDensity(Random random, BinaryVector obs) {
    int bit = chooseBit(random);
    byte weight = inputsToWeights.get(bit);
    boolean inputActive = obs.getEntry(bit) > 0;
    if ((weight == -1 && inputActive) || (weight == +1 && !inputActive)) {
      inputsToWeights.put(bit, (byte) (weight * -1));
      threshold -= 1;
    }
  }

  private int chooseBit(Random random) {
    return inputs[random.nextInt(inputs.length)];
  }

  @Override
  public LTUAdaptive newLTU(int index, int[] inputs, byte[] weights) {
    return new LTUAdaptive(index, inputs, weights, minFrequency, maxFrequency);
  }
}
