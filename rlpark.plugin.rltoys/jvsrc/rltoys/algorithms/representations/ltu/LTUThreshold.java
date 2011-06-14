package rltoys.algorithms.representations.ltu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class LTUThreshold implements LTU {
  private static final long serialVersionUID = -4100313691365362138L;
  final static public double Beta = .6;
  final public int index;
  protected final Map<Integer, Byte> inputsToWeights = new HashMap<Integer, Byte>();
  protected double threshold;
  private int sum;
  private boolean isActive;

  public LTUThreshold() {
    index = -1;
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
}
