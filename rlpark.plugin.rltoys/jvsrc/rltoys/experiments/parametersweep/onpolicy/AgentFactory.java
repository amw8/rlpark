package rltoys.experiments.parametersweep.onpolicy;

import java.io.Serializable;
import java.util.Random;

import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.labels.Labeled;

public interface AgentFactory extends Labeled, Serializable {
  public RLAgent createAgent(RLProblem problem, Parameters parameters, Random random);
}
