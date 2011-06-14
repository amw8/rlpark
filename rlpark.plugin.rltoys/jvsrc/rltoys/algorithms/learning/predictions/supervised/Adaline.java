package rltoys.algorithms.learning.predictions.supervised;

import rltoys.algorithms.learning.predictions.LearningAlgorithm;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.monitoring.wrappers.Squared;

@Monitor
public class Adaline implements LearningAlgorithm {
  private static final long serialVersionUID = -1427180343679219960L;
  private final double alpha;
  @Monitor(level = 4)
  private final PVector weights;
  private double prediction;
  private double target;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  private double error;

  public Adaline(int size, double alpha) {
    weights = new PVector(size);
    this.alpha = alpha;
  }

  @Override
  public double learn(RealVector x, double y) {
    prediction = predict(x);
    target = y;
    error = target - prediction;
    weights.addToSelf(x.mapMultiply(alpha * error));
    return error;
  }

  @Override
  public double predict(RealVector x) {
    return weights.dotProduct(x);
  }

  public PVector weights() {
    return weights;
  }
}
