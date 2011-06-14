package rltoys.algorithms.representations.features.envio;

import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.TStep;

public class ActionFeature extends StepFeature {
  private static final long serialVersionUID = -4740897381492857575L;
  private final int actionIndex;

  public ActionFeature(int actionIndex) {
    super(String.format("a%d", actionIndex));
    this.actionIndex = actionIndex;
  }

  @Override
  public double computeValue(TStep step) {
    if (step.a_t == null)
      return 0.0;
    return ((ActionArray) step.a_t).actions[actionIndex];
  }
}
