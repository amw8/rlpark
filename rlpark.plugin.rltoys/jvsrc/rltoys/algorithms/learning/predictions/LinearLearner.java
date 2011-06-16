package rltoys.algorithms.learning.predictions;

import rltoys.math.vector.PVector;

public interface LinearLearner {
  void resetWeight(int index);

  PVector weights();

  double error();
}
