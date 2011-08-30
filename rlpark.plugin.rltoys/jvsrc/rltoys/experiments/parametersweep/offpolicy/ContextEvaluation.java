package rltoys.experiments.parametersweep.offpolicy;

import rltoys.environments.envio.Runner;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.AgentFactory;
import rltoys.experiments.parametersweep.interfaces.ProblemFactory;
import rltoys.experiments.parametersweep.offpolicy.internal.OffPolicyEvaluationContext;
import rltoys.experiments.parametersweep.offpolicy.internal.OffPolicyEvaluator;
import rltoys.experiments.parametersweep.offpolicy.internal.SweepJob;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgent;
import rltoys.experiments.parametersweep.reinforcementlearning.RewardMonitor;

public class ContextEvaluation extends AbstractContextOffPolicy implements OffPolicyEvaluationContext {
  private static final long serialVersionUID = -593900122821568271L;
  private final int nbRewardCheckpoint;

  public ContextEvaluation(ProblemFactory environmentFactory, AgentFactory agentFactory, int nbRewardCheckpoint,
      int maxTimeStepsOffPolicyEval) {
    super(environmentFactory, agentFactory, maxTimeStepsOffPolicyEval);
    this.nbRewardCheckpoint = nbRewardCheckpoint;
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }

  @Override
  public RewardMonitor connectBehaviourRewardMonitor(Runner runner, Parameters parameters) {
    RewardMonitor behaviourMonitor = new RewardMonitor("Behaviour", nbRewardCheckpoint,
                                                       parameters.maxEpisodeTimeSteps(), parameters.nbEpisode());
    runner.onTimeStep.connect(behaviourMonitor);
    return behaviourMonitor;
  }

  @Override
  public RewardMonitor connectTargetRewardMonitor(int counter, Runner runner, Parameters parameters) {
    OffPolicyAgent agent = (OffPolicyAgent) runner.agent();
    final int maxTimeSteps = (int) parameters.get(MaxTimeStepsOffPolicyEval);
    OffPolicyEvaluator evaluator = new OffPolicyEvaluator(createEnvironment(counter), agent, maxTimeSteps);
    RewardMonitor targetMonitor = new RewardMonitor("Target", nbRewardCheckpoint, parameters.maxEpisodeTimeSteps(),
                                                    parameters.nbEpisode());
    evaluator.connectRewardMonitor(runner, targetMonitor);
    return targetMonitor;
  }
}
