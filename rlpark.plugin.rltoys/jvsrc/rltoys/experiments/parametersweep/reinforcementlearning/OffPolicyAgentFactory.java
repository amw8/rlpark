package rltoys.experiments.parametersweep.reinforcementlearning;

import java.io.Serializable;
import java.util.Random;

import rltoys.algorithms.representations.Projector;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.labels.Labeled;

public interface OffPolicyAgentFactory extends Serializable, Labeled {
  public OffPolicyAgent createAgent(Projector projector, Parameters parameters, Random random);
}
