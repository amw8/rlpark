package rltoys.algorithms.learning.control;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public class GreedyControl implements Control, Predictor {
  private static final long serialVersionUID = 5423438227384324715L;
  private final Policy acting;
  private final RealVector theta;

  public GreedyControl(RealVector theta, Policy acting) {
    this.theta = theta;
    this.acting = acting;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    return acting.decide(x_tp1);
  }

  @Override
  public double predict(RealVector x) {
    return theta.dotProduct(x);
  }

  public Policy acting() {
    return acting;
  }
}
