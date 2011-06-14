package rlpark.plugin.robot.statemachine;

import rltoys.environments.envio.observations.TStep;

public abstract class TimedState implements StateNode<TStep> {
  final private int nbTimeSteps;
  private int currentTimeSteps;

  public TimedState(int nbTimeSteps) {
    this.nbTimeSteps = nbTimeSteps;
  }

  @Override
  public boolean isDone() {
    return currentTimeSteps >= nbTimeSteps;
  }

  @Override
  public void start() {
    currentTimeSteps = 0;
  }

  @Override
  public void step(TStep step) {
    currentTimeSteps++;
  }

  protected int currentTimeSteps() {
    return currentTimeSteps;
  }
}
