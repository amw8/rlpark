package rltoys.experiments.parametersweep.reinforcementlearning;

import java.io.Serializable;

import rltoys.algorithms.representations.Projector;
import rltoys.environments.envio.problems.RLProblem;

public interface ProjectorFactory extends Serializable {
  Projector createProjector(RLProblem problem);
}
