package rltoys.experiments.parametersweep.offpolicy.internal;

import rltoys.environments.envio.Runner;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;

public interface OffPolicyEvaluationContext extends ReinforcementLearningContext {
  AgentEvaluator connectBehaviourRewardMonitor(Runner runner, Parameters parameters);

  AgentEvaluator connectTargetRewardMonitor(int counter, Runner runner, Parameters parameters);
}
