package rltoys.algorithms.learning.control.acting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.Constants;
import rltoys.math.vector.RealVector;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class Greedy implements Policy, MonitorContainer {
  private static final long serialVersionUID = 1675962692054005355L;
  public final double tolerance;
  protected Random random;
  protected final StateToStateAction toStateAction;
  protected final Map<Action, Double> actionValues = new LinkedHashMap<Action, Double>();
  protected final List<Action> bestActions = new ArrayList<Action>();
  protected final Predictor predictor;
  protected final Action[] availableActions;

  public Greedy(Predictor predictor, Action[] actions, StateToStateAction toStateAction) {
    this(null, actions, predictor, toStateAction);
  }


  public Greedy(Random random, Action[] actions, Predictor predictor, StateToStateAction toStateAction) {
    this(random, predictor, actions, toStateAction, Constants.EPSILON);
  }

  public Greedy(Random random, Predictor predictor, Action[] actions, StateToStateAction toStateAction,
      double tolerance) {
    this.toStateAction = toStateAction;
    this.random = random;
    this.tolerance = tolerance;
    this.predictor = predictor;
    availableActions = actions;
  }

  @Override
  public Action decide(RealVector s) {
    return pickupBestAction(s);
  }

  protected Action pickupBestAction(RealVector s_tp1) {
    double bestValue = -Double.MAX_VALUE;
    for (Action a : availableActions) {
      RealVector phi_sa = toStateAction.stateAction(s_tp1, a);
      double value = phi_sa != null ? predictor.predict(phi_sa) : 0.0;
      assert Utils.checkValue(value);
      actionValues.put(a, value);
      if (value > bestValue)
        bestValue = value;
    }
    bestActions.clear();
    for (Map.Entry<Action, Double> entry : actionValues.entrySet())
      if (bestValue - entry.getValue() <= tolerance) {
        bestActions.add(entry.getKey());
        if (random == null)
          break;
      }
    assert bestActions.size() > 0;
    int actionChoosen = random != null ? random.nextInt(bestActions.size()) : 0;
    return bestActions.get(actionChoosen);
  }

  @Override
  public String toString() {
    return actionValues.toString();
  }

  @Override
  public double pi(RealVector s, Action a) {
    pickupBestAction(s);
    if (bestActions.contains(a))
      return 1.0 / bestActions.size();
    return 0.0;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    for (Action a : availableActions) {
      final Action current = a;
      monitor.add(Labels.label(a), 0, new Monitored() {
        @Override
        public double monitoredValue() {
          Double value = actionValues.get(current);
          return value != null ? value : 0;
        }
      });
    }
    final double nbActions = availableActions.length;
    monitor.add(Labels.label("bestActionsRatio"), 0, new Monitored() {
      @Override
      public double monitoredValue() {
        return bestActions.size() / nbActions;
      }
    });
  }
}
