package rltoys.experiments.continuousaction;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLProblem;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.normalization.IncMeanVarNormalizer;
import rltoys.math.vector.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class NoStateExperiment {
  @Monitor
  public final Control control;
  private TRStep step;
  private final PVector x;
  @Monitor
  protected double reward;
  @Monitor
  protected final IncMeanVarNormalizer averageReward;
  private final RLProblem environment;

  public NoStateExperiment(RLProblem environnment, Control control) {
    this.control = control;
    averageReward = new IncMeanVarNormalizer(1);
    this.environment = environnment;
    step = environnment.initialize();
    x = new PVector(1.0);
  }

  public TRStep step() {
    PVector x_t = step.isEpisodeStarting() ? null : x;
    reward = step.r_tp1;
    Action a_tp1 = control.step(x_t, step.a_t, x, step.r_tp1);
    step = environment.step(a_tp1);
    averageReward.update(step.r_tp1);
    return step;
  }

  static public double evaluateActorCritic(int nbTimeSteps, RLProblem environment, Control actorCritic) {
    NoStateExperiment experiment = new NoStateExperiment(environment, actorCritic);
    for (int t = 0; t < nbTimeSteps; t++)
      experiment.step();
    return experiment.averageReward.mean();
  }
}
