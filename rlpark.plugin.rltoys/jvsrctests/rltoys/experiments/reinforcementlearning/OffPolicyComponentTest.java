package rltoys.experiments.reinforcementlearning;

import java.util.Random;

import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.acting.RandomPolicy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgent;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProjectorFactory;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

@SuppressWarnings("serial")
public class OffPolicyComponentTest {
  static class OffPolicyLearnerTest implements OffPolicyLearner {
    @Override
    public void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double reward) {
    }

    @Override
    public Action proposeAction(RealVector x_t) {
      return RLProblemFactoryTest.Action02;
    }
  }

  static class ProjectorFactoryTest implements ProjectorFactory {
    @Override
    public Projector createProjector(RLProblem problem) {
      return new Projector() {
        @Override
        public RealVector project(double[] ds) {
          return ds != null ? new PVector(1.0) : null;
        }
      };
    }
  }

  static class OffPolicyAgentFactoryTest implements OffPolicyAgentFactory {
    @Override
    public String label() {
      return "OffPolicyAgent";
    }

    @Override
    public OffPolicyAgent createAgent(RLProblem problem, Projector projector, Parameters parameters, Random random) {
      OffPolicyLearner learner = new OffPolicyLearnerTest();
      Policy behaviour = new RandomPolicy(random, new Action[] { RLProblemFactoryTest.Action01 });
      return new OffPolicyAgent(projector, behaviour, learner);
    }
  }
}
