package rltoys.algorithms.learning.control.sarsa;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.SVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ExpectedSarsa implements Predictor {
  private static final long serialVersionUID = 6062163881538174939L;
  protected final Sarsa sarsa;
  protected final StateToStateAction toStateAction;
  @Monitor
  private final Policy policy;
  private final Action[] actions;

  public ExpectedSarsa(Action[] actions, Policy policy, StateToStateAction toStateAction, Sarsa sarsa) {
    this.policy = policy;
    this.toStateAction = toStateAction;
    this.sarsa = sarsa;
    this.actions = actions;
  }

  public double update(RealVector s_t, Action a_t, double r_tp1, RealVector s_tp1) {
    SVector phi_bar_tp1 = null;
    if (s_t != null && s_tp1 != null) {
      phi_bar_tp1 = new SVector(sarsa.theta.size);
      for (Action a : actions) {
        RealVector phi_stp1a = toStateAction.stateAction(s_tp1, a);
        phi_bar_tp1.addToSelf(phi_stp1a.mapMultiply(policy.pi(s_tp1, a)));
      }
    }
    RealVector phi_stat = toStateAction.stateAction(s_t, a_t);
    return sarsa.update(phi_stat, phi_bar_tp1, r_tp1);
  }

  @Override
  public double predict(RealVector x) {
    return sarsa.predict(x);
  }

  public PVector theta() {
    return sarsa.theta;
  }
}
