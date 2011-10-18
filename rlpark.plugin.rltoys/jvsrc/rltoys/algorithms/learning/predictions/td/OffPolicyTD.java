package rltoys.algorithms.learning.predictions.td;


import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

public interface OffPolicyTD extends Predictor, LinearLearner {
  double update(double rho_t, RealVector x_t, RealVector x_tp1, double r_tp1);

  double prediction();

  PVector secondaryWeights();
}
