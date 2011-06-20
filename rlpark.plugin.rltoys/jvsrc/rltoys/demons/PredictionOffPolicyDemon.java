package rltoys.demons;

import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.learning.predictions.td.GTD;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class PredictionOffPolicyDemon implements Demon {
  private static final long serialVersionUID = 2103050204892958885L;
  private final RewardFunction rewardFunction;
  @Monitor
  private final GTD gtd;
  @Monitor
  protected final Policy target;
  protected final Policy behaviour;
  @Monitor
  private double rho_t;

  public PredictionOffPolicyDemon(RewardFunction rewardFunction, GTD gtd, Policy target, Policy behaviour) {
    this.rewardFunction = rewardFunction;
    this.gtd = gtd;
    this.target = target;
    this.behaviour = behaviour;
  }

  @Override
  public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
    rho_t = a_t != null ? target.pi(x_t, a_t) / behaviour.pi(x_t, a_t) : 0;
    gtd.update(x_t, x_tp1, rewardFunction.reward(), rho_t);
  }

  public RewardFunction rewardFunction() {
    return rewardFunction;
  }

  public Predictor predicter() {
    return gtd;
  }

  public Policy targetPolicy() {
    return target;
  }

  @Override
  public LinearLearner learner() {
    return gtd;
  }
}
