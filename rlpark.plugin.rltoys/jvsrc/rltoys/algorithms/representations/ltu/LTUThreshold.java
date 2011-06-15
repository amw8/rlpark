package rltoys.algorithms.representations.ltu;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import rltoys.math.vector.BinaryVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class LTUThreshold implements LTUAdaptiveDensity {
  private static final long serialVersionUID = -4100313691365362138L;
  final static public double Beta = .6;
  final public int index;
  protected final Map<Integer, Byte> inputsToWeights = new HashMap<Integer, Byte>();
  protected double threshold;
  private int sum;
  private boolean isActive;
  final private int[] inputs;

  public LTUThreshold() {
    index = -1;
    inputs = null;
  }

  public LTUThreshold(int index, int[] inputs, byte[] weights) {
    this.index = index;
    int nbNegative = 0;
    for (int i = 0; i < inputs.length; i++) {
      inputsToWeights.put(inputs[i], weights[i]);
      if (weights[i] < 0)
        nbNegative++;
    }
    threshold = -nbNegative + 0.6 * inputs.length;
    this.inputs = inputs.clone();
  }

  @Override
  public void setActiveInput(int activeInput) {
    sum += inputsToWeights.get(activeInput);
  }

  @Override
  public void update() {
    isActive = sum > threshold;
    sum = 0;
  }

  @Override
  public LTUThreshold newLTU(int index, int[] inputs, byte[] weights) {
    return new LTUThreshold(index, inputs, weights);
  }

  @Override
  public Set<Integer> inputs() {
    return inputsToWeights.keySet();
  }

  @Override
  public int index() {
    return index;
  }

  @Override
  public boolean isActive() {
    return isActive;
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
}
