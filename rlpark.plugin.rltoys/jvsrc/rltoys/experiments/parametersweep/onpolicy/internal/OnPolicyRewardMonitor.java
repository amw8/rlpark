package rltoys.experiments.parametersweep.onpolicy.internal;

import rltoys.environments.envio.Runner;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;

public interface OnPolicyRewardMonitor extends AgentEvaluator {
  void connect(Runner runner);
}
