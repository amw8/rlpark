package rltoys.demons;

import rltoys.algorithms.learning.control.gq.GreedyGQ;
import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ControlOffPolicyDemon implements Demon {
  private static final long serialVersionUID = -7997723890930214800L;
  private final RewardFunction rewardFunction;
  private final OutcomeFunction outcomeFunction;
  @Monitor
  private final GreedyGQ gq;

  public ControlOffPolicyDemon(RewardFunction rewardFunction, GreedyGQ gq) {
    this(new OutcomeFunction.DefaultOutcomeFunction(), rewardFunction, gq);
  }

  public ControlOffPolicyDemon(OutcomeFunction outcomeFunction, RewardFunction rewardFunction, GreedyGQ gq) {
    this.rewardFunction = rewardFunction;
    this.gq = gq;
    this.outcomeFunction = outcomeFunction;
  }

  @Override
  public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
    gq.update(x_t, a_t, rewardFunction.reward(), outcomeFunction.outcome(), x_tp1, a_t);
  }

  public RewardFunction rewardFunction() {
    return rewardFunction;
  }

  public OutcomeFunction outcomeFunction() {
    return outcomeFunction;
  }

  public Predictor predictor() {
    return gq.predictor();
  }

  public Policy targetPolicy() {
    return gq.targetPolicy();
  }

  @Override
  public LinearLearner learner() {
    return gq.gq();
  }
}
