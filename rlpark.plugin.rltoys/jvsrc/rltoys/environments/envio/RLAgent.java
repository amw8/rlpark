package rltoys.environments.envio;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.TRStep;

public interface RLAgent extends Serializable {
  Action getAtp1(TRStep step);
}
