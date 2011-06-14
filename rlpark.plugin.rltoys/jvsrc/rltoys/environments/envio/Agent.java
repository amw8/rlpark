package rltoys.environments.envio;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.TStep;

public interface Agent {
  Action getAtp1(TStep step);
}
