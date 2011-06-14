package rltoys.algorithms.representations.features;

import java.util.List;
import java.util.Random;

import rltoys.math.representations.Function;

public class UniformRandom implements Feature {

  private static final long serialVersionUID = 597356751436305767L;
  private final Random random;
  private final double mean;
  private final double variance;
  private double value;

  public UniformRandom(Random random, double mean, double variance) {
    assert random != null;
    this.random = random;
    this.mean = mean;
    this.variance = variance;
    update();
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public List<Function> dependencies() {
    return null;
  }

  @Override
  public void update() {
    value = random.nextDouble() * variance * 2 - variance + mean;
  }

  @Override
  public String toString() {
    return String.format("URand(%f[%.2f/%.2f])", value, mean, variance);
  }
}
