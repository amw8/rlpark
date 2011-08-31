package rltoys.algorithms.learning.control;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public interface OffPolicyControl extends Serializable {
  void learn(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1, Action a_tp1);

  Action proposeAction(RealVector x_t);
}