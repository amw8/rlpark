package rltoys.experiments.parametersweep.onpolicy.internal;

import rltoys.environments.envio.Runner;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.parameters.Parameters;

public interface ContextOnPolicy extends Context {
  Runner createRunner(int currentIndex, Parameters parameters);

  RewardMonitor createRewardMonitor(Parameters parameters);
}
