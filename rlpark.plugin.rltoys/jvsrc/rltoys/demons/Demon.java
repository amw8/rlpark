package rltoys.demons;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public interface Demon extends Serializable {
  void update(RealVector x_t, Action a_t, RealVector x_tp1);
}
