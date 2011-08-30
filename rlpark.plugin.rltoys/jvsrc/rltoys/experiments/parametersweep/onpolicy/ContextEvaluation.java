package rltoys.experiments.parametersweep.onpolicy;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.AgentFactory;
import rltoys.experiments.parametersweep.interfaces.ProblemFactory;
import rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyEvaluationContext;
import rltoys.experiments.parametersweep.onpolicy.internal.SweepJob;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.RewardMonitor;

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
  public RewardMonitor createRewardMonitor(Parameters parameters) {
    return new RewardMonitor(nbRewardCheckpoint, parameters.maxEpisodeTimeSteps(), parameters.nbEpisode());
  }
}
