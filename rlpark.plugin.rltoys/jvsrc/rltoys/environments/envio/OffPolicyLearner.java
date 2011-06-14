package rltoys.environments.envio;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.TStep;

public interface OffPolicyLearner {
  void learn(TStep step, Action a_tp1);

  Action proposeAction(TStep o_tp1);
}
