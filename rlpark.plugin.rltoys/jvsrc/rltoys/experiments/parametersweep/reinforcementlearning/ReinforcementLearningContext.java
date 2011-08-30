package rltoys.experiments.parametersweep.reinforcementlearning;

import rltoys.environments.envio.Runner;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.parameters.Parameters;

public interface ReinforcementLearningContext extends Context {
  Runner createRunner(int currentIndex, Parameters parameters);
}
