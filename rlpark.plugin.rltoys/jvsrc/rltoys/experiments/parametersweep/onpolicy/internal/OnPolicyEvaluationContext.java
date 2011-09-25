package rltoys.experiments.parametersweep.onpolicy.internal;

import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;

public interface OnPolicyEvaluationContext extends ReinforcementLearningContext {
  OnPolicyRewardMonitor createRewardMonitor(Parameters parameters);
}
