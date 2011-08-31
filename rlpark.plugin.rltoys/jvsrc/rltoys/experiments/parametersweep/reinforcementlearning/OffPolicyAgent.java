package rltoys.experiments.parametersweep.reinforcementlearning;

import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.vector.RealVector;

public class OffPolicyAgent implements RLAgent {
  private static final long serialVersionUID = -3161971141054100394L;
  private final Projector projector;
  private RealVector x_t;
  private final OffPolicyLearner learner;
  private final Policy behaviour;

  public OffPolicyAgent(Projector projector, Policy behaviour, OffPolicyLearner learner) {
    this.projector = projector;
    this.learner = learner;
    this.behaviour = behaviour;
  }

  @Override
  public Action getAtp1(TRStep step) {
    RealVector x_tp1 = projector.project(step.o_tp1);
    Action a_tp1 = behaviour.decide(x_tp1);
    learner.learn(x_t, step.a_t, x_tp1, a_tp1, step.r_tp1);
    x_t = x_tp1;
    return a_tp1;
  }

  public OffPolicyLearner offpolicyLearner() {
    return learner;
  }
}
