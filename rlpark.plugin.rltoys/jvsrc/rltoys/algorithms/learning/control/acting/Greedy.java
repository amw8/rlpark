package rltoys.algorithms.learning.control.acting;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;

public class Greedy implements Policy {
  private static final long serialVersionUID = 1675962692054005355L;
  protected final StateToStateAction toStateAction;
  protected final Predictor predictor;
  protected final Action[] availableActions;
  protected Action bestAction;
  private double bestValue;

  public Greedy(Predictor predictor, Action[] actions, StateToStateAction toStateAction) {
    this.toStateAction = toStateAction;
    this.predictor = predictor;
    availableActions = actions;
  }

  @Override
  public Action decide(RealVector s) {
    return pickupBestAction(s);
  }

  protected Action pickupBestAction(RealVector s_tp1) {
    if (s_tp1 == null)
      return bestAction;
    bestAction = null;
    for (Action a : availableActions) {
      RealVector phi_sa = toStateAction.stateAction(s_tp1, a);
      double value = predictor.predict(phi_sa);
      if (bestAction == null || value > bestValue) {
        bestValue = value;
        bestAction = a;
      }
    }
    return bestAction;
  }

  @Override
  public double pi(RealVector s, Action a) {
    pickupBestAction(s);
    return a == bestAction ? 1 : 0;
  }
}
