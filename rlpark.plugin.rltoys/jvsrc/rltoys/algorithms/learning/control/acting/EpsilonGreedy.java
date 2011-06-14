package rltoys.algorithms.learning.control.acting;

import java.util.Random;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.Constants;
import rltoys.math.vector.RealVector;
import rltoys.utils.Utils;

public class EpsilonGreedy extends Greedy {
  private static final long serialVersionUID = -2618584767896890494L;
  private final double epsilon;

  public EpsilonGreedy(Random random, Action[] actions, Predictor predictor, StateToStateAction toStateAction,
      double epsilon) {
    this(random, actions, toStateAction, predictor, epsilon, Constants.EPSILON);
  }

  public EpsilonGreedy(Random random, Action[] actions, StateToStateAction toStateAction, Predictor predictor,
      double epsilon, double tolerance) {
    super(random, predictor, actions, toStateAction, tolerance);
    this.epsilon = epsilon;
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
    if (bestActions.contains(a))
      probability += (1.0 - epsilon) / bestActions.size();
    return probability + epsilon / availableActions.length;
  }

  public Action[] actions() {
    return availableActions;
  }
}
