package rltoys.algorithms.learning.predictions.td;


import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;

public interface OnPolicyTD extends Predictor {
  double update(RealVector phi_t, RealVector phi_tp1, double r_tp1);

  PVector theta();
}
