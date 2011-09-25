package rltoys.experiments.parametersweep.onpolicy;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyEvaluationContext;
import rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyRewardMonitor;
import rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitors;
import rltoys.experiments.parametersweep.onpolicy.internal.SweepJob;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;

public class ContextEvaluation extends AbstractContextOnPolicy implements OnPolicyEvaluationContext {
  private static final long serialVersionUID = -5926779335932073094L;
  private final int nbRewardCheckpoint;

  public ContextEvaluation(ProblemFactory environmentFactory, AgentFactory agentFactory, int nbRewardCheckpoint) {
    super(environmentFactory, agentFactory);
    this.nbRewardCheckpoint = nbRewardCheckpoint;
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }

  @Override
  public OnPolicyRewardMonitor createRewardMonitor(Parameters parameters) {
    return RewardMonitors.create(nbRewardCheckpoint, parameters);
  }
}
