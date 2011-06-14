package rltoys.algorithms.representations.features.envio;

import rltoys.environments.envio.observations.TStep;

public class ObservationFeature extends StepFeature {
  private static final long serialVersionUID = 5100208523995265008L;
  private final int inputIndex;

  public ObservationFeature(int inputIndex) {
    this(String.format("o%d", inputIndex), inputIndex);
  }

  public ObservationFeature(String label, int inputIndex) {
    super(label);
    this.inputIndex = inputIndex;
  }

  @Override
  public double computeValue(TStep step) {
    return step.o_tp1[inputIndex];
  }
}
