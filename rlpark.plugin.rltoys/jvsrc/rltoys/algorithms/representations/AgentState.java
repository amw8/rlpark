package rltoys.algorithms.representations;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.observations.Observation;
import rltoys.math.vector.RealVector;

public interface AgentState extends Serializable {
  RealVector update(Action a_t, Observation o_tp1);

  double stateNorm();

  int stateSize();
}
