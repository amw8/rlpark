package rltoys.algorithms.learning.control.acting;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public class UnknownPolicy implements Policy {
  private static final long serialVersionUID = -4805473070123975706L;
  private final Policy policy;

  public UnknownPolicy(Policy policy) {
    this.policy = policy;
  }

  @Override
  public double pi(RealVector s, Action a) {
    return 1.0;
  }

  @Override
  public Action decide(RealVector s) {
    return policy.decide(s);
  }
}
