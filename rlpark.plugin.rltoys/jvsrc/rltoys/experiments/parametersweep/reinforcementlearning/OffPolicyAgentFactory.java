package rltoys.experiments.parametersweep.reinforcementlearning;

import java.io.Serializable;
import java.util.Random;

import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.labels.Labeled;

public interface OffPolicyAgentFactory extends Serializable, Labeled {
  OffPolicyAgent createAgent(RLProblem problem, Projector projector, Parameters parameters, Policy behaviourPolicy,
      Random random);
}
