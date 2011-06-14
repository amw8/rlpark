package rltoys.environments.envio;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;

public interface RLProblem {
  TRStep initialize();

  TRStep step(Action action);

  Legend legend();
}
