package rltoys.environments.envio;

import java.io.Serializable;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public interface OffPolicyLearner extends Serializable {
  void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double reward);

  Action proposeAction(RealVector x_t);

  Policy targetPolicy();

  Predictor predictor();
}
