package rltoys.algorithms.learning.control.gq;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.OffPolicyControl;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public class GQOffPolicyControl implements Control, OffPolicyControl {
  private static final long serialVersionUID = -1080045423180429474L;
  private final ExpectedGQ gq;

  public GQOffPolicyControl(ExpectedGQ gq) {
    this.gq = gq;
  }

  public Policy acting() {
    return gq.target;
  }

  @Override
  public void learn(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1, Action a_tp1) {
    gq.update(x_t, a_t, r_tp1, 0.0, x_tp1, a_tp1);
  }

  @Override
  public Action proposeAction(RealVector x_t) {
    return acting().decide(x_t);
  }

  @Override
  public Action step(RealVector s_t, Action a_t, RealVector s_tp1, double r_tp1) {
    learn(s_t, a_t, s_tp1, r_tp1, null);
    return proposeAction(s_tp1);
  }
}
