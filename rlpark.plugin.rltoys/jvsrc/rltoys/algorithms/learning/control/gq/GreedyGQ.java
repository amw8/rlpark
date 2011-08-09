package rltoys.algorithms.learning.control.gq;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class GreedyGQ implements Predictor {
  private static final long serialVersionUID = -8310145173177914856L;

  @Monitor
  protected final GQ gq;
  @Monitor
  protected final Policy target;
  protected final Policy behaviour;
  protected final StateToStateAction toStateAction;
  @Monitor
  public double rho_t;

  public GreedyGQ(GQ gq, StateToStateAction toStateAction, Policy target, Policy behaviour) {
    this.gq = gq;
    this.target = target;
    this.behaviour = behaviour;
    this.toStateAction = toStateAction;
  }

  public double update(RealVector s_t, Action a_t, double r_tp1, double z_tp1, RealVector s_tp1, Action a_tp1) {
    rho_t = a_t != null ? target.pi(s_t, a_t) / behaviour.pi(s_t, a_t) : 0;
    RealVector sa_t = toStateAction.stateAction(s_t, a_t);
    RealVector sa_tp1 = toStateAction.stateAction(s_tp1, a_tp1);
    return gq.update(sa_t, rho_t, r_tp1, sa_tp1, z_tp1);
  }

  @Override
  public double predict(RealVector x) {
    return gq.predict(x);
  }

  public PVector theta() {
    return gq.theta;
  }

  public double gamma() {
    return 1 - gq.beta_tp1;
  }

  public GQ gq() {
    return gq;
  }

  public Policy targetPolicy() {
    return target;
  }
}
