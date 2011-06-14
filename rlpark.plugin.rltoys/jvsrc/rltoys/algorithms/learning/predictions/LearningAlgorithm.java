package rltoys.algorithms.learning.predictions;

import rltoys.math.vector.RealVector;

public interface LearningAlgorithm extends Predictor {
  double learn(RealVector x, double y);
}
