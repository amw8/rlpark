package learning;

import java.util.Random;

import rltoys.algorithms.learning.control.actorcritic.onpolicy.Actor;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistributionSkewed;
import rltoys.algorithms.learning.predictions.td.OnPolicyTD;
import rltoys.algorithms.learning.predictions.td.TDLambda;
import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.pendulum.SwingPendulum;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class ActorCriticPendulum implements Runnable {
  private final SwingPendulum problem;
  private final ActorCritic actorCritic;
  private final TileCodersNoHashing tileCoders;
  private final Clock clock = new Clock("ActorCriticPendulum");

  public ActorCriticPendulum() {
    problem = new SwingPendulum(new Random(0), false);
    tileCoders = new TileCodersNoHashing(problem.getObservationRanges());
    tileCoders.addFullTilings(10, 10);
    double gamma = 0.99;
    double lambda = .3;
    int nbActive = tileCoders.nbActive();
    int vectorSize = tileCoders.vectorSize();
    OnPolicyTD critic = new TDLambda(lambda, gamma, .1 / nbActive, vectorSize);
    PolicyDistribution policyDistribution = new NormalDistributionSkewed(new Random(0), 0.0, 1.0);
    Actor actor = new Actor(policyDistribution, 0.01 / nbActive, vectorSize);
    actorCritic = new ActorCritic(critic, actor);
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    TRStep step = problem.initialize();
    Action action = null;
    RealVector x_t = null;
    while (clock.tick()) {
      BinaryVector x_tp1 = tileCoders.project(step.o_tp1);
      action = actorCritic.step(x_t, step.a_t, x_tp1, step.r_tp1);
      step = problem.step(action);
      x_t = x_tp1;
    }
  }

  public static void main(String[] args) {
    new ActorCriticPendulum().run();
  }
}
