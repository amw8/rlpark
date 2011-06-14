package rltoys.algorithms.representations.acting;

import java.util.Arrays;
import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import rltoys.utils.Utils;

public class RandomPolicy implements PolicyDistribution {
  private static final long serialVersionUID = 7993101579423392389L;
  private final Random random;
  private final Action[] actions;

  public RandomPolicy(Random random, Action[] actions) {
    this.random = random;
    this.actions = actions.clone();
  }

  @Override
  public double pi(RealVector s, Action a) {
    assert Arrays.asList(a).contains(a);
    return 1.0 / actions.length;
  }

  @Override
  public Action decide(RealVector s) {
    return Utils.choose(random, actions);
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    return new PVector[] { new PVector(1) };
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    return new PVector[] { new PVector(1) };
  }
}
