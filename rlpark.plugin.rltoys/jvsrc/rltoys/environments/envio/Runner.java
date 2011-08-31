package rltoys.environments.envio;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
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
    assert runnerEvent.nbTotalTimeSteps == 0;
    assert runnerEvent.episode == 0;
    while (runnerEvent.episode < nbEpisode)
      runEpisode();
    return runnerEvent;
  }

  public void runEpisode() {
    assert runnerEvent.step == null;
    do {
      step();
    } while (runnerEvent.step != null);
  }

  public void step() {
    assert runnerEvent.episode < nbEpisode;
    // When we start a new episode
    if (runnerEvent.step == null) {
      runnerEvent.step = environment.initialize();
      assert runnerEvent.step.isEpisodeStarting();
    }
    // Fire the time step event
    onTimeStep.fire(runnerEvent);
    Action action = agent.getAtp1(runnerEvent.step);
    // Fire the end of an episode
    if (runnerEvent.step.isEpisodeEnding()) {
      onEpisodeEnd.fire(runnerEvent);
      runnerEvent.episode += 1;
    }
    runnerEvent.step = !runnerEvent.step.isEpisodeEnding() ? environment.step(action) : null;
    runnerEvent.nbTotalTimeSteps++;
    // Maximum number of steps in the episode reached
    if (runnerEvent.step != null && runnerEvent.step.time == maxEpisodeTimeSteps)
      runnerEvent.step = runnerEvent.step.createEndingStep();
  }

  public RunnerEvent runnerEvent() {
    return runnerEvent;
  }

  public RLAgent agent() {
    return agent;
  }
}
