package rltoys.experiments.parametersweep.offpolicy.internal;

import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgent;
import rltoys.experiments.parametersweep.reinforcementlearning.RewardMonitor;
import zephyr.plugin.core.api.signals.Listener;

public class OffPolicyEvaluator {
  class EvaluatedAgent implements RLAgent {
    private final OffPolicyLearner learner;
    private final Projector projector;

    public EvaluatedAgent(OffPolicyAgent offpolicyAgent) {
      learner = offpolicyAgent.offpolicyLearner();
      projector = offpolicyAgent.projector();
    }

    @Override
    public Action getAtp1(TRStep step) {
      return learner.proposeAction(projector.project(step.o_tp1));
    }
  }

  final Runner targetRunner;

  public OffPolicyEvaluator(RLProblem problem, OffPolicyAgent offpolicyAgent, int maxEpisodeTimeSteps) {
    EvaluatedAgent agent = new EvaluatedAgent(offpolicyAgent);
    targetRunner = new Runner(problem, agent, Integer.MAX_VALUE, maxEpisodeTimeSteps);
  }

  public void connectRewardMonitor(Runner behaviourRunner, RewardMonitor rewardMonitor) {
    behaviourRunner.onTimeStep.connect(new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        targetRunner.step();
      }
    });
    targetRunner.onTimeStep.connect(rewardMonitor);
  }
}
