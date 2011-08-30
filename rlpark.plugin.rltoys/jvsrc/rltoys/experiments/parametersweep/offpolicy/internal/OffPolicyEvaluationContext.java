package rltoys.experiments.parametersweep.offpolicy.internal;

import rltoys.environments.envio.Runner;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;
import rltoys.experiments.parametersweep.reinforcementlearning.RewardMonitor;

public interface OffPolicyEvaluationContext extends ReinforcementLearningContext {
  RewardMonitor connectBehaviourRewardMonitor(Runner runner, Parameters parameters);

  RewardMonitor connectTargetRewardMonitor(int counter, Runner runner, Parameters parameters);
}
