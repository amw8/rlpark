package rltoys.algorithms.learning.control;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public interface Control extends Serializable {
  Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1);

  Action proposeAction(RealVector x);
}