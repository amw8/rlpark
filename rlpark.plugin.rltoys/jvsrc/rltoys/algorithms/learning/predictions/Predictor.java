package rltoys.algorithms.learning.predictions;

import java.io.Serializable;

import rltoys.math.vector.RealVector;

public interface Predictor extends Serializable {
  public double predict(RealVector x);
}
