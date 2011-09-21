package rltoys.experiments.parametersweep.offpolicy;

import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.Runner;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rltoys.experiments.parametersweep.offpolicy.internal.OffPolicyEvaluationContext;
import rltoys.experiments.parametersweep.offpolicy.internal.SweepJob;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgent;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProjectorFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.internal.RewardMonitor;

public class ContextEvaluation extends AbstractContextOffPolicy implements OffPolicyEvaluationContext {
  private static final long serialVersionUID = -593900122821568271L;

  public ContextEvaluation(OffPolicyProblemFactory environmentFactory, ProjectorFactory projectorFactory,
      OffPolicyAgentFactory agentFactory, OffPolicyEvaluation evaluation) {
    super(environmentFactory, projectorFactory, agentFactory, evaluation);
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }

  @Override
  public RewardMonitor connectBehaviourRewardMonitor(Runner runner, Parameters parameters) {
    RewardMonitor behaviourMonitor = new RewardMonitor("Behaviour", evaluation.nbRewardCheckpoint(),
                                                       parameters.maxEpisodeTimeSteps(), parameters.nbEpisode());
    runner.onTimeStep.connect(behaviourMonitor);
    return behaviourMonitor;
  }

  @Override
  public AgentEvaluator connectTargetRewardMonitor(int counter, Runner runner, Parameters parameters) {
    OffPolicyAgent agent = (OffPolicyAgent) runner.agent();
    OffPolicyLearner learner = agent.offpolicyLearner();
    return evaluation.connectEvaluator(counter, runner, environmentFactory, projectorFactory, learner, parameters);
  }
}
