package rltoys.algorithms.learning.predictions.td;


import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.math.vector.RealVector;

public interface OnPolicyTD extends Predictor, LinearLearner {
  double update(RealVector phi_t, RealVector phi_tp1, double r_tp1);

  double prediction();
}
