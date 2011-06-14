package rltoys.algorithms.representations.acting;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public class ConstantPolicy extends StochasticPolicy {

  private static final long serialVersionUID = 9106677500699183729L;
  private final LinkedHashMap<Action, Double> distribution;

  public ConstantPolicy(Random random, Map<Action, Double> distribution) {
    super(random);
    this.distribution = new LinkedHashMap<Action, Double>(distribution);
  }

  @Override
  public double pi(RealVector s, Action a) {
    return distribution.get(a);
  }

  @Override
  public Action decide(RealVector s) {
    return chooseAction(distribution);
  }
}
