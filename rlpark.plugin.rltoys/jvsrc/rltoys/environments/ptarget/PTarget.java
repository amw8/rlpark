package rltoys.environments.ptarget;

import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.utils.Utils;

public class PTarget implements RLProblem {

  static final double Resolution = 0.01;

  protected class Target {
    final protected double position;

    protected Target() {
      position = newPosition();
    }

    protected double newPosition() {
      return (random.nextFloat() - 0.5) * 2;
    }

    @Override
    public String toString() {
      return String.format("T:%f", position);
    }
  }

  protected final Target[] targets;
  protected final double[] currentPosition;
  private final double[] currentObservation;
  final Random random;
  private double distance;
  private final Legend legend;
  private TRStep lastTStep;

  public PTarget(Random random, int nbTarget) {
    this.random = random;
    targets = new Target[nbTarget];
    currentPosition = new double[nbTarget];
    currentObservation = new double[nbTarget];
    for (int i = 0; i < nbTarget; i++)
      targets[i] = new Target();
    legend = createLegend(nbTarget);
  }

  private Legend createLegend(int nbTarget) {
    String[] labels = new String[nbTarget];
    for (int i = 0; i < nbTarget; i++)
      labels[i] = "Target" + String.valueOf(i);
    return new Legend(labels);
  }

  @Override
  public TRStep initialize() {
    for (int i = 0; i < targets.length; i++)
      currentPosition[i] = targets[i].newPosition();
    distance = computeDistance();
    lastTStep = new TRStep(currentObservation(), -distance);
    return lastTStep;
  }

  @Override
  public TRStep step(Action action) {
    assert action != null;
    ActionArray actionArray = (ActionArray) action;
    assert actionArray.actions.length == targets.length;
    for (int i = 0; i < actionArray.actions.length; i++) {
      double a = actionArray.actions[i];
      assert Utils.checkValue(a);
      currentPosition[i] += a;
      currentPosition[i] = Math.min(currentPosition[i], 1);
      currentPosition[i] = Math.max(currentPosition[i], -1);
    }
    distance = computeDistance();
    if (isFinished())
      lastTStep = new TRStep(lastTStep, action, null, -distance);
    else
      lastTStep = new TRStep(lastTStep, action, currentObservation(), -distance);
    return lastTStep;
  }

  public double[] currentObservation() {
    for (int i = 0; i < currentPosition.length; i++)
      currentObservation[i] = currentPosition[i] - targets[i].position;
    return currentObservation;
  }

  private boolean isFinished() {
    return computeDistance() < Resolution;
  }

  double computeDistance() {
    double sum = 0;
    for (int i = 0; i < targets.length; i++) {
      double di = targets[i].position - currentPosition[i];
      sum += di * di;
    }
    return Math.sqrt(sum);
  }

  @Override
  public Legend legend() {
    return legend;
  }

  public int stateSize() {
    return currentPosition.length;
  }

  public int actionSize() {
    return targets.length;
  }
}
