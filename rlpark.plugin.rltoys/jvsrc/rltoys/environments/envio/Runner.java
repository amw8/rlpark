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

  private void runEpisode() {
    assert isEpisodeEnding();
    do {
      step();
    } while (!isEpisodeEnding());
  }

  public void step() {
    assert runnerEvent.episode < nbEpisode;
    // When we have a new episode (i.e the last episode ended)
    if (isEpisodeEnding()) {
      runnerEvent.step = environment.initialize();
      assert runnerEvent.step.isEpisodeStarting();
    }
    // Normal time step
    onTimeStep.fire(runnerEvent);
    Action action = agent.getAtp1(runnerEvent.step);
    runnerEvent.step = environment.step(action);
    runnerEvent.nbTotalTimeSteps++;
    // Maximum number of steps in the episode reached
    if (maxEpisodeTimeSteps > 0 && runnerEvent.step.time >= maxEpisodeTimeSteps)
      runnerEvent.step = runnerEvent.step.createEndingStep();
    // When an episode is ending
    if (isEpisodeEnding()) {
      onEpisodeEnd.fire(runnerEvent);
      runnerEvent.episode += 1;
    }
  }


  protected boolean isEpisodeEnding() {
    return runnerEvent.step == null || runnerEvent.step.isEpisodeEnding();
  }


  public RunnerEvent runnerEvent() {
    return runnerEvent;
  }

  public RLAgent agent() {
    return agent;
  }
}
