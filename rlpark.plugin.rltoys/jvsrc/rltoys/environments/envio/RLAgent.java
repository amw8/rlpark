package rltoys.environments.envio;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.TRStep;

public interface RLAgent {
  Action getAtp1(TRStep step);
}
