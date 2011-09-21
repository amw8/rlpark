package rltoys.algorithms.learning.control.acting;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.StochasticPolicy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class SoftMax extends StochasticPolicy implements MonitorContainer {
  private static final long serialVersionUID = -2129719316562814077L;
  final protected Map<Action, Double> actionDistribution = new LinkedHashMap<Action, Double>();
  private final Map<Action, RealVector> phis_sa = new LinkedHashMap<Action, RealVector>();
  private final StateToStateAction toStateAction;
  private final double tau;
  private final Predictor predictor;
  private final Action[] availableActions;

  public SoftMax(Random random, Predictor predictor, Action[] actions, StateToStateAction toStateAction, double tau) {
    super(random);
    this.toStateAction = toStateAction;
    this.tau = tau;
    this.predictor = predictor;
    availableActions = actions;
  }

  public SoftMax(Random random, Predictor predictor, Action[] actions, StateToStateAction toStateAction) {
    this(random, predictor, actions, toStateAction, 1);
  }

  @Override
  public Action decide(RealVector s) {
    updateActionDistribution(s);
    return chooseAction(actionDistribution);
  }

  private void updateActionDistribution(RealVector s) {
    actionDistribution.clear();
    phis_sa.clear();
    double sum = 0.0;
    for (Action action : availableActions) {
      RealVector phi_sa = toStateAction.stateAction(s, action);
      phis_sa.put(action, phi_sa);
      double value = Math.exp(predictor.predict(phi_sa) / tau);
      sum += value;
      actionDistribution.put(action, value);
    }
    for (Map.Entry<Action, Double> entry : actionDistribution.entrySet())
      actionDistribution.put(entry.getKey(), entry.getValue() / sum);
  }

  @Override
  public double pi(RealVector s, Action a) {
    updateActionDistribution(s);
    return actionDistribution.get(a);
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    for (Action a : availableActions) {
      final Action current = a;
      monitor.add(Labels.label(a), 0, new Monitored() {
        @Override
        public double monitoredValue() {
          Double value = actionDistribution.get(current);
          return value != null ? value : 0;
        }
      });
    }
  }
}
