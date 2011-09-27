package rltoys.algorithms.learning.control.gq;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.environments.envio.OffPolicyLearner;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.SVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class GreedyGQ implements OffPolicyLearner {
  private static final long serialVersionUID = 7017521530598253457L;
  @Monitor
  protected final GQ gq;
  @Monitor
  protected final Policy target;
  protected final Policy behaviour;
  protected final StateToStateAction toStateAction;
  @Monitor
  public double rho_t;
  private final Action[] actions;


  public GreedyGQ(GQ gq, Action[] actions, StateToStateAction toStateAction, Policy target, Policy behaviour) {
    this.gq = gq;
    this.target = target;
    this.behaviour = behaviour;
    this.toStateAction = toStateAction;
    this.actions = actions;

  }

  public double update(RealVector s_t, Action a_t, double r_tp1, double z_tp1, RealVector s_tp1, Action a_tp1) {
    rho_t = 0.0;
    if (a_t != null)
      rho_t = target.pi(s_t, a_t) / behaviour.pi(s_t, a_t);
    SVector sa_bar_tp1 = new SVector(gq.v.size);
    if (s_t != null && s_tp1 != null) {
      for (Action a : actions) {
        RealVector sa_tp1 = toStateAction.stateAction(s_tp1, a);
        sa_bar_tp1.addToSelf(sa_tp1.mapMultiply(target.pi(s_tp1, a)));
      }
    }
    RealVector phi_stat = toStateAction.stateAction(s_t, a_t);
    return gq.update(phi_stat, rho_t, r_tp1, sa_bar_tp1, z_tp1);
  }

  public PVector theta() {
    return gq.v;
  }

  public double gamma() {
    return 1 - gq.beta_tp1;
  }

  public GQ gq() {
    return gq;
  }

  @Override
  public Policy targetPolicy() {
    return target;
  }

  @Override
  public void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double reward) {
    update(x_t, a_t, reward, 0, x_tp1, a_tp1);
  }

  @Override
  public Action proposeAction(RealVector x_t) {
    return target.decide(x_t);
  }

  @Override
  public Predictor predictor() {
    return gq;
  }
}
