package rltoys.algorithms.learning.control.gq;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.SVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class ExpectedGQ extends GreedyGQ {
  private static final long serialVersionUID = 7525004433866502851L;
  private final Action[] actions;

  public ExpectedGQ(GQ gq, Action[] actions, StateToStateAction toStateAction, Policy target, Policy behaviour) {
    super(gq, toStateAction, target, behaviour);
    this.actions = actions;
  }

  @Override
  public double update(RealVector s_t, Action a_t, double r_tp1, double z_tp1, RealVector s_tp1, Action a_tp1) {
    rho_t = 0.0;
    if (a_t != null)
      rho_t = target.pi(s_t, a_t) / behaviour.pi(s_t, a_t);
    SVector sa_bar_tp1 = null;
    if (s_t != null && s_tp1 != null) {
      sa_bar_tp1 = new SVector(gq.theta.size);
      for (Action a : actions) {
        RealVector sa_tp1 = toStateAction.stateAction(s_tp1, a);
        sa_bar_tp1.addToSelf(sa_tp1.mapMultiply(target.pi(s_tp1, a)));
      }
    }
    RealVector phi_stat = toStateAction.stateAction(s_t, a_t);
    return gq.update(phi_stat, rho_t, r_tp1, sa_bar_tp1, z_tp1);
  }
}
