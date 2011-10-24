package rltoys.horde.demons;

import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.learning.predictions.td.GTD;
import rltoys.algorithms.learning.predictions.td.GVF;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.horde.functions.ConstantGamma;
import rltoys.horde.functions.ConstantOutcomeFunction;
import rltoys.horde.functions.GammaFunction;
import rltoys.horde.functions.OutcomeFunction;
import rltoys.horde.functions.RewardFunction;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class PredictionOffPolicyDemon implements Demon, Labeled {
  private static final long serialVersionUID = 2103050204892958885L;
  private final RewardFunction rewardFunction;
  @Monitor
  private final GVF gtd;
  @Monitor
  protected final Policy target;
  protected final Policy behaviour;
  @Monitor
  private double rho_t;
  private final OutcomeFunction outcomeFunction;
  private final GammaFunction gammaFunction;

  public PredictionOffPolicyDemon(Policy target, Policy behaviour, GTD gtd, RewardFunction rewardFunction) {
    this(target, behaviour, gtd, rewardFunction, new ConstantGamma(gtd.gamma()), new ConstantOutcomeFunction(0));
  }

  public PredictionOffPolicyDemon(Policy target, Policy behaviour, GVF gtd, RewardFunction rewardFunction,
      GammaFunction gammaFunction, OutcomeFunction outcomeFunction) {
    this.rewardFunction = rewardFunction;
    this.gammaFunction = gammaFunction;
    this.outcomeFunction = outcomeFunction;
    this.gtd = gtd;
    this.target = target;
    this.behaviour = behaviour;
  }

  @Override
  public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
    rho_t = a_t != null ? target.pi(x_t, a_t) / behaviour.pi(x_t, a_t) : 0;
    gtd.update(rho_t, x_t, x_tp1, rewardFunction.reward(), gammaFunction.gamma(), outcomeFunction.outcome());
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

  @Override
  public String label() {
    return "offpolicyDemon" + Labels.label(rewardFunction);
  }
}
