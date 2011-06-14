package rltoys.experiments.parametersweep.onpolicy;

import java.io.Serializable;
import java.util.Random;

import rltoys.environments.envio.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.labels.Labeled;

public interface ProblemFactory extends Labeled, Serializable {
  RLProblem createEnvironment(Random random);

  void setExperimentParameters(Parameters parameters);
}
