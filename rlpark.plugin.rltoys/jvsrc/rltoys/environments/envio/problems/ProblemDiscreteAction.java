package rltoys.environments.envio.problems;

import rltoys.algorithms.representations.actions.Action;

public interface ProblemDiscreteAction extends RLProblem {
  Action[] actions();
}
