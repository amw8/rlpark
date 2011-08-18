package rltoys.algorithms.representations.ltu.units;

import java.util.Random;

import rltoys.math.vector.implementations.SVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class LTUThreshold implements LTUAdaptiveDensity {
  private static final long serialVersionUID = -4100313691365362138L;
  final static public double Beta = .6;
  final public int index;
  protected final SVector connections;
  protected double threshold;
  private double sum;
  private boolean isActive;

  public LTUThreshold() {
    index = -1;
    connections = null;
  }

  public LTUThreshold(int index, int[] inputs, byte[] weights) {
    this.index = index;
    int nbNegative = 0;
    connections = new SVector(inputs.length, inputs.length);
    for (int i = 0; i < inputs.length; i++) {
      connections.setEntry(inputs[i], weights[i]);
      if (weights[i] < 0)
        nbNegative++;
    }
    threshold = -nbNegative + 0.6 * inputs.length;
  }

  @Override
  public void updateSum(double[] inputVector) {
    sum = connections.dotProduct(inputVector);
  }

  @Override
  public LTUThreshold newLTU(int index, int[] inputs, byte[] weights) {
    return new LTUThreshold(index, inputs, weights);
  }

  @Override
  public int[] inputs() {
    return connections.activeIndexes();
  }

  @Override
  public int index() {
    return index;
  }

  @Override
  public void decreaseDensity(Random random, double[] inputVector) {
    int bit = random.nextInt(connections.nonZeroElements());
    double weight = connections.values[bit];
    boolean inputActive = inputVector[connections.indexes[bit]] > 0;
    if ((weight == 1 && inputActive) || (weight == -1 && !inputActive)) {
      connections.values[bit] *= -1;
      threshold += 1;
    }
  }

  @Override
  public void increaseDensity(Random random, double[] inputVector) {
    int bit = random.nextInt(connections.nonZeroElements());
    double weight = connections.values[bit];
    boolean inputActive = inputVector[connections.indexes[bit]] > 0;
    if ((weight == -1 && inputActive) || (weight == +1 && !inputActive)) {
      connections.values[bit] *= -1;
      threshold -= 1;
    }
  }

  @Override
  public boolean updateActivation() {
    isActive = sum >= threshold;
    sum = 0;
    return isActive;
  }

  @Override
  public boolean isActive() {
    return isActive;
  }
}
