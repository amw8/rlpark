package rltoys.environments.envio;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.TRStep;
import zephyr.plugin.core.api.signals.Signal;

public class Runner implements Serializable {
  private static final long serialVersionUID = 465593140388569561L;

  static public class RunnerEvent {
    public int nbTotalTimeSteps = 0;
    public int episode = 0;
    public TRStep step = null;

    @Override
    public String toString() {
      return String.format("Ep(%d): %s on %d", episode, step, nbTotalTimeSteps);
    }
  }

  public final Signal<RunnerEvent> onEpisodeEnd = new Signal<RunnerEvent>();
  public final Signal<RunnerEvent> onTimeStep = new Signal<RunnerEvent>();
  protected final RunnerEvent runnerEvent = new RunnerEvent();
  private final RLAgent agent;
  private final RLProblem environment;
  private final int maxEpisodeTimeSteps;
  private final int nbEpisode;

  public Runner(RLProblem environment, RLAgent agent, int nbEpisode, int maxEpisodeTimeSteps) {
    this.environment = environment;
    this.agent = agent;
    this.nbEpisode = nbEpisode;
    this.maxEpisodeTimeSteps = maxEpisodeTimeSteps;
  }


  public RunnerEvent run() {
    start();
    while (runnerEvent.episode < nbEpisode)
      runEpisode();
    return runnerEvent;
  }

  private void start() {
    assert runnerEvent.nbTotalTimeSteps == 0;
    assert runnerEvent.episode == 0;
    runnerEvent.step = environment.initialize();
    assert runnerEvent.step.isEpisodeStarting();
  }

  private void runEpisode() {
    runnerEvent.step = environment.initialize();
    assert runnerEvent.step.isEpisodeStarting();
    while (!isEpisodeEnding()) {
      onTimeStep.fire(runnerEvent);
      Action action = agent.getAtp1(runnerEvent.step);
      runnerEvent.step = environment.step(action);
      runnerEvent.nbTotalTimeSteps++;
    }
    onEpisodeEnd.fire(runnerEvent);
    runnerEvent.episode += 1;
  }


  protected boolean isEpisodeEnding() {
    if (runnerEvent.step.isEpisodeEnding())
      return true;
    if (maxEpisodeTimeSteps <= 0)
      return false;
    if (runnerEvent.step.time < maxEpisodeTimeSteps)
      return false;
    runnerEvent.step = runnerEvent.step.createEndingStep();
    return true;
  }


  public RunnerEvent runnerEvent() {
    return runnerEvent;
  }
}
