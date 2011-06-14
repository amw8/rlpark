package rltoys.algorithms.representations.acting;

import java.util.Map;
import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.utils.Utils;

public abstract class StochasticPolicy implements Policy {

  private static final long serialVersionUID = 6747532059495537542L;
  protected final Random random;

  public StochasticPolicy(Random random) {
    this.random = random;
  }

  protected Action chooseAction(Map<Action, Double> distribution) {
    assert Utils.checkDistribution(distribution.values());
    double randomValue = random.nextDouble();
    double sum = 0;
    for (Map.Entry<Action, Double> entry : distribution.entrySet()) {
      sum += entry.getValue();
      if (sum >= randomValue)
        return entry.getKey();
    }
    assert false;
    return null;
  }
}
