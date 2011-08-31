package rltoys.experiments.parametersweep.offpolicy.evaluation;

import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProjectorFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.internal.RewardMonitor;
import zephyr.plugin.core.api.signals.Listener;

public class EpisodeBasedOffPolicyEvaluation extends AbstractOffPolicyEvaluation {
  private static final long serialVersionUID = -654783411988105997L;
  private final int maxTimeStepsPerEpisode;
  private final int nbEvaluation;

  public EpisodeBasedOffPolicyEvaluation(int nbBehaviourRewardCheckpoint, int nbEvaluation, int maxTimeStepsPerEpisode) {
    super(nbBehaviourRewardCheckpoint);
    this.nbEvaluation = nbEvaluation;
    this.maxTimeStepsPerEpisode = maxTimeStepsPerEpisode;
  }

  @Override
  public AgentEvaluator connectEvaluator(final int counter, Runner behaviourRunner,
      final ProblemFactory problemFactory, final ProjectorFactory projectorFactory, final OffPolicyLearner learner,
      final Parameters parameters) {
    RewardMonitor rewardMonitor = new RewardMonitor("Target", nbEvaluation, maxTimeStepsPerEpisode, nbEvaluation);
    RLProblem problem = createProblem(counter, problemFactory);
    RLAgent agent = createEvaluatedAgent(problem, projectorFactory, learner);
    final Runner runner = new Runner(problem, agent, nbEvaluation, maxTimeStepsPerEpisode);
    runner.onTimeStep.connect(rewardMonitor);
    final int nbEpisodeBetweenEvaluation = Math.max(1, parameters.nbEpisode() / nbEvaluation);
    behaviourRunner.onEpisodeEnd.connect(new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        if (eventInfo.episode % nbEpisodeBetweenEvaluation == 0)
          runner.runEpisode();
      }
    });
    return rewardMonitor;
  }
}
