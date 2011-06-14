package rltoys.demons;

import rltoys.algorithms.learning.predictions.td.TD;
import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class PredictionDemon implements Demon, Labeled {
  private static final long serialVersionUID = -6966208035134604865L;
  private final RewardFunction rewardFunction;
  @Monitor
  private final TD td;

  public PredictionDemon(RewardFunction rewardFunction, TD td) {
    this.rewardFunction = rewardFunction;
    this.td = td;
  }

  @Override
  public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
    td.update(x_t, x_tp1, rewardFunction.reward());
  }

  public double prediction() {
    return td.v_t();
  }

  public RewardFunction rewardFunction() {
    return rewardFunction;
  }

  public TD predicter() {
    return td;
  }

  @Override
  public String label() {
    return "demon" + Labels.label(rewardFunction) + String.valueOf(td.gamma());
  }
}
