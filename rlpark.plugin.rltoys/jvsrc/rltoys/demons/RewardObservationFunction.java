package rltoys.demons;

import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class RewardObservationFunction implements RewardFunction, Labeled {
  private static final long serialVersionUID = -5930168576876015871L;
  @Monitor
  private double reward;
  private final int observationIndex;
  private final String label;

  public RewardObservationFunction(Legend legend, String label) {
    this.label = label;
    observationIndex = legend.indexOf(label);
    assert observationIndex >= 0;
  }

  public void update(double[] o) {
    reward = o[observationIndex];
  }

  @Override
  public double reward() {
    return reward;
  }

  @Override
  public String label() {
    return label;
  }
}
