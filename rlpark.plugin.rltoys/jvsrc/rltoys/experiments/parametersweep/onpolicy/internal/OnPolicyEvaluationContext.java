package rltoys.experiments.parametersweep.onpolicy.internal;

import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;
import rltoys.experiments.parametersweep.reinforcementlearning.internal.RewardMonitor;

public interface OnPolicyEvaluationContext extends ReinforcementLearningContext {
  RewardMonitor createRewardMonitor(Parameters parameters);
}
