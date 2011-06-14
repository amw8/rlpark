package rltoys.algorithms.learning.control.actorcritic.onpolicy;

import rltoys.algorithms.learning.predictions.td.OnPolicyTD;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class AverageRewardCritic implements OnPolicyTD {
  private static final long serialVersionUID = 6722116765396296040L;
  private final OnPolicyTD td;
  private final double alpha_j;
  private double j = 0.0;

  public AverageRewardCritic(OnPolicyTD td, double alpha_j) {
    this.td = td;
    this.alpha_j = alpha_j;
  }

  @Override
  public double predict(RealVector x) {
    return td.predict(x);
  }

  @Override
  public double update(RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    if (phi_t == null)
      return 0.0;
    j = (1 - alpha_j) * j + alpha_j * r_tp1;
    return td.update(phi_t, phi_tp1, r_tp1 - j);
  }

  @Override
  public PVector theta() {
    return td.theta();
  }
}
