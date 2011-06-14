package rltoys.environments.mountaincar;

import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLProblemBounded;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.ranges.Range;

public class MountainCar implements RLProblemBounded {
  protected static final ActionArray LEFT = new ActionArray(-1.0);
  protected static final ActionArray RIGHT = new ActionArray(1.0);
  protected static final ActionArray STOP = new ActionArray(0.0);
  protected static final Action[] Actions = { STOP, RIGHT, LEFT };

  protected static final String VELOCITY = "velocity";
  protected static final String POSITION = "position";
  protected static final Legend legend = new Legend(POSITION, VELOCITY);

  protected Double position = null;
  protected double velocity = 0.0;
  protected static final Range positionRange = new Range(-1.2, 0.6);
  protected static final Range velocityRange = new Range(-0.07, 0.07);

  private static final double target = positionRange.max();
  private double throttleFactor = 1.0;
  private final Random random;
  private TRStep lastTStep;
  private final int episodeLengthMax;

  public MountainCar(Random random) {
    this(random, -1);
  }

  public MountainCar(Random random, int episodeLengthMax) {
    this.random = random;
    this.episodeLengthMax = episodeLengthMax;
  }

  protected void update(ActionArray action) {
    double actionThrottle = Math.min(Math.max(action.actions[0], -1.0), 1.0);
    double throttle = actionThrottle * throttleFactor;
    velocity = velocityRange.bound(velocity + 0.001 * throttle - 0.0025 * Math.cos(3 * position));
    position += velocity;
    if (position < positionRange.min())
      velocity = 0.0;
    position = positionRange.bound(position);
  }

  @Override
  public TRStep step(Action action) {
    assert position != null;
    if (action != null)
      update((ActionArray) action);
    TRStep tstep;
    if (endOfEpisode()) {
      position = null;
      tstep = new TRStep(lastTStep, action, null, 0.0);
    } else
      tstep = new TRStep(lastTStep, action, new double[] { position, velocity }, -1.0);
    lastTStep = tstep;
    return tstep;
  }

  private boolean endOfEpisode() {
    return position >= target || (episodeLengthMax > 0 && lastTStep != null && lastTStep.time > episodeLengthMax);
  }

  @Override
  public TRStep initialize() {
    if (random == null) {
      position = -0.5;
      velocity = 0.0;
    } else {
      position = positionRange.choose(random);
      velocity = velocityRange.choose(random);
    }
    lastTStep = new TRStep(new double[] { position, velocity }, -1);
    return lastTStep;
  }

  @Override
  public Legend legend() {
    return legend;
  }

  public Action[] actions() {
    return Actions;
  }

  public void setThrottleFactor(double factor) {
    throttleFactor = factor;
  }

  @Override
  public Range[] getObservationRanges() {
    return new Range[] { positionRange, velocityRange };
  }
}
