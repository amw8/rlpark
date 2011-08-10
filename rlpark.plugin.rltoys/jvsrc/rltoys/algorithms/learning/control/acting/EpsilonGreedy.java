package rltoys.algorithms.learning.control.acting;

import java.util.Random;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;
import rltoys.utils.Utils;

public class EpsilonGreedy extends Greedy {
  private static final long serialVersionUID = -2618584767896890494L;
  private final double epsilon;
  private final Random random;

  public EpsilonGreedy(Random random, Action[] actions, StateToStateAction toStateAction, Predictor predictor,
      double epsilon) {
    super(predictor, actions, toStateAction);
    this.epsilon = epsilon;
    this.random = random;
  }

  @Override
  public Action decide(RealVector s) {
    Action bestAction = pickupBestAction(s);
    if (random.nextFloat() < epsilon)
      return Utils.choose(random, availableActions);
    return bestAction;
  }

  @Override
  public double pi(RealVector s, Action a) {
    pickupBestAction(s);
    double probability = 0.0;
    if (a == bestAction)
      return 1.0 - epsilon;
    return probability + epsilon / availableActions.length;
  }

  public Action[] actions() {
    return availableActions;
  }
}
